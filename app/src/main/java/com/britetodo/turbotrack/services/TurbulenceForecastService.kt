package com.britetodo.turbotrack.services

import com.britetodo.turbotrack.data.model.Airport
import com.britetodo.turbotrack.data.model.DailyForecast
import com.britetodo.turbotrack.data.model.TurbulenceForecast
import com.britetodo.turbotrack.data.model.TurbulenceForecastLayer
import com.britetodo.turbotrack.data.model.TurbulenceForecastPoint
import com.britetodo.turbotrack.data.model.TurbulenceSeverity
import com.britetodo.turbotrack.data.model.TurbulenceType
import com.google.gson.JsonObject
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Pressure levels → approximate flight levels
private val PRESSURE_LEVELS = listOf(
    Triple(200, 390, 39_000),   // hPa, FL, ft
    Triple(250, 340, 34_000),
    Triple(300, 300, 30_000),
    Triple(400, 240, 24_000),
    Triple(500, 180, 18_000),
    Triple(700, 100, 10_000)
)

@Singleton
class TurbulenceForecastService @Inject constructor(
    private val openMeteoService: OpenMeteoService,
    private val aviationService: AviationWeatherService
) {

    /**
     * Wind shear algorithm — exact port from iOS TurboTrack:
     * 1. Decompose wind into u/v components
     * 2. shear = √[(Δu)² + (Δv)²] / Δh  in kt/1000ft
     * 3. Jet stream amplification: >80kt → ×1.3, >60kt → ×1.15
     * 4. Classify: ≥8=EXTREME, 6–8=SEVERE, 4–6=MODERATE, <4=LIGHT
     */
    private fun computeWindShear(
        speed1: Double, dir1: Double,    // upper layer
        speed2: Double, dir2: Double,    // lower layer
        altDiff1000ft: Double            // altitude difference in 1000ft units
    ): Double {
        val u1 = -speed1 * sin(Math.toRadians(dir1))
        val v1 = -speed1 * cos(Math.toRadians(dir1))
        val u2 = -speed2 * sin(Math.toRadians(dir2))
        val v2 = -speed2 * cos(Math.toRadians(dir2))

        val deltaU = u1 - u2
        val deltaV = v1 - v2
        var shear = sqrt(deltaU * deltaU + deltaV * deltaV) / altDiff1000ft

        // Jet stream amplification
        val avgSpeed = (speed1 + speed2) / 2.0
        shear *= when {
            avgSpeed > 80 -> 1.3
            avgSpeed > 60 -> 1.15
            else -> 1.0
        }

        return shear
    }

    suspend fun getForecast(origin: Airport, destination: Airport): TurbulenceForecast {
        // Sample points along the route (midpoint + endpoints)
        val midLat = (origin.lat + destination.lat) / 2
        val midLon = (origin.lon + destination.lon) / 2

        // Fetch Open-Meteo for midpoint
        val json = openMeteoService.getForecast(
            latitude = midLat,
            longitude = midLon
        )

        val hourly = json.getAsJsonObject("hourly")
        val layers = computeLayers(hourly)
        val days = computeDailyForecasts(hourly)
        val overallSeverity = layers.maxByOrNull { it.windShear }?.severity ?: TurbulenceSeverity.NONE
        val primaryType = determinePrimaryType(layers)

        // Count PIREPs near route
        val pirepCount = try {
            aviationService.getPireps().body()?.size ?: 0
        } catch (e: Exception) { 0 }

        return TurbulenceForecast(
            origin = origin,
            destination = destination,
            overallSeverity = overallSeverity,
            primaryType = primaryType,
            days = days,
            layers = layers,
            pirepCount = pirepCount,
            sigmetActive = false
        )
    }

    private fun computeLayers(hourly: JsonObject): List<TurbulenceForecastLayer> {
        val layers = mutableListOf<TurbulenceForecastLayer>()

        for (i in 0 until PRESSURE_LEVELS.size - 1) {
            val (hPa1, fl1, ft1) = PRESSURE_LEVELS[i]
            val (hPa2, fl2, ft2) = PRESSURE_LEVELS[i + 1]

            val speeds1 = getHourlyArray(hourly, "wind_speed_${hPa1}hPa")
            val dirs1 = getHourlyArray(hourly, "wind_direction_${hPa1}hPa")
            val speeds2 = getHourlyArray(hourly, "wind_speed_${hPa2}hPa")
            val dirs2 = getHourlyArray(hourly, "wind_direction_${hPa2}hPa")

            if (speeds1.isEmpty() || speeds2.isEmpty()) continue

            val avgSpeed1 = speeds1.average()
            val avgDir1 = dirs1.average()
            val avgSpeed2 = speeds2.average()
            val avgDir2 = dirs2.average()
            val altDiff = (ft1 - ft2) / 1000.0

            val shear = computeWindShear(avgSpeed1, avgDir1, avgSpeed2, avgDir2, altDiff)
            val severity = TurbulenceSeverity.fromWindShear(shear)

            layers.add(
                TurbulenceForecastLayer(
                    flightLevel = fl1,
                    altitudeFt = ft1,
                    pressureHpa = hPa1,
                    severity = severity,
                    windShear = shear,
                    windSpeed = avgSpeed1,
                    windDirection = avgDir1
                )
            )
        }

        return layers.sortedBy { it.flightLevel }
    }

    private fun computeDailyForecasts(hourly: JsonObject): List<DailyForecast> {
        val speeds200 = getHourlyArray(hourly, "wind_speed_200hPa")
        val dirs200 = getHourlyArray(hourly, "wind_direction_200hPa")
        val speeds300 = getHourlyArray(hourly, "wind_speed_300hPa")
        val dirs300 = getHourlyArray(hourly, "wind_direction_300hPa")

        val today = LocalDate.now()
        return (0..2).map { dayOffset ->
            val startHour = dayOffset * 24
            val endHour = minOf(startHour + 24, speeds200.size)
            if (startHour >= speeds200.size) {
                return@map DailyForecast(
                    date = today.plusDays(dayOffset.toLong()),
                    severity = TurbulenceSeverity.NONE,
                    maxWindShear = 0.0,
                    primaryType = TurbulenceType.CAT,
                    hourlyPoints = emptyList()
                )
            }
            val daySpeed200 = speeds200.subList(startHour, endHour)
            val dayDir200 = dirs200.subList(startHour, endHour)
            val daySpeed300 = speeds300.subList(startHour, endHour)
            val dayDir300 = dirs300.subList(startHour, endHour)

            val shears = daySpeed200.indices.map { h ->
                computeWindShear(daySpeed200[h], dayDir200[h], daySpeed300[h], dayDir300[h], 4.0)
            }
            val maxShear = shears.maxOrNull() ?: 0.0
            val avgShear = shears.average()

            DailyForecast(
                date = today.plusDays(dayOffset.toLong()),
                severity = TurbulenceSeverity.fromWindShear(avgShear),
                maxWindShear = maxShear,
                primaryType = TurbulenceType.CAT,
                hourlyPoints = emptyList()
            )
        }
    }

    private fun getHourlyArray(hourly: JsonObject, key: String): List<Double> {
        return try {
            hourly.getAsJsonArray(key)?.map { it.asDouble } ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    private fun determinePrimaryType(layers: List<TurbulenceForecastLayer>): TurbulenceType {
        val maxShearLayer = layers.maxByOrNull { it.windShear } ?: return TurbulenceType.CAT
        return when {
            maxShearLayer.flightLevel >= 300 -> TurbulenceType.CAT
            maxShearLayer.windSpeed > 60 -> TurbulenceType.CAT
            else -> TurbulenceType.CONVECTIVE
        }
    }
}
