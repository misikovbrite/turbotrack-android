package com.britetodo.turbotrack.data.model

import java.time.LocalDate

data class TurbulenceForecast(
    val origin: Airport,
    val destination: Airport,
    val overallSeverity: TurbulenceSeverity,
    val primaryType: TurbulenceType,
    val days: List<DailyForecast>,
    val layers: List<TurbulenceForecastLayer>,
    val pirepCount: Int,
    val sigmetActive: Boolean,
    val generatedAt: Long = System.currentTimeMillis()
)

data class DailyForecast(
    val date: LocalDate,
    val severity: TurbulenceSeverity,
    val maxWindShear: Double,
    val primaryType: TurbulenceType,
    val hourlyPoints: List<TurbulenceForecastPoint>
)

data class TurbulenceForecastLayer(
    val flightLevel: Int,          // e.g. 100, 180, 240, 300, 340, 390
    val altitudeFt: Int,
    val pressureHpa: Int,
    val severity: TurbulenceSeverity,
    val windShear: Double,
    val windSpeed: Double,
    val windDirection: Double
)

data class TurbulenceForecastPoint(
    val hour: Int,                 // 0-23
    val severity: TurbulenceSeverity,
    val windShear: Double,
    val windSpeed: Double,
    val windDirection: Double,
    val lat: Double,
    val lon: Double
)
