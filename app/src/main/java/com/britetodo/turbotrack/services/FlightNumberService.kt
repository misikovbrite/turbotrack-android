package com.britetodo.turbotrack.services

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

// ── API Models ────────────────────────────────────────────────────────────────

data class AeroDataBoxAirportInfo(
    @SerializedName("icao") val icao: String? = null,
    @SerializedName("iata") val iata: String? = null,
    @SerializedName("municipalityName") val municipalityName: String? = null,
    @SerializedName("name") val name: String? = null
)

data class AeroDataBoxEndpoint(
    @SerializedName("airport") val airport: AeroDataBoxAirportInfo? = null,
    @SerializedName("scheduledTime") val scheduledTime: Map<String, String>? = null
)

data class AeroDataBoxAirline(
    @SerializedName("name") val name: String? = null
)

data class AeroDataBoxFlight(
    @SerializedName("departure") val departure: AeroDataBoxEndpoint? = null,
    @SerializedName("arrival") val arrival: AeroDataBoxEndpoint? = null,
    @SerializedName("airline") val airline: AeroDataBoxAirline? = null,
    @SerializedName("number") val number: String? = null
)

// ── Retrofit Interface ────────────────────────────────────────────────────────

interface FlightNumberApiService {
    @GET("flights/number/{flightNumber}/{date}")
    suspend fun getFlightByNumber(
        @Path("flightNumber") flightNumber: String,
        @Path("date") date: String,
        @Header("x-rapidapi-key") apiKey: String = "1f130de935msh1ef2c824de03bdap1f4263jsn079555a14636",
        @Header("x-rapidapi-host") apiHost: String = "aerodatabox.p.rapidapi.com",
        @Query("withAircraftImage") withAircraftImage: Boolean = false,
        @Query("withLocation") withLocation: Boolean = false
    ): List<AeroDataBoxFlight>
}

// ── Domain Model ──────────────────────────────────────────────────────────────

data class FlightRoute(
    val departureIcao: String,
    val arrivalIcao: String,
    val departureCity: String,
    val arrivalCity: String,
    val airline: String,
    val cachedAt: Long
)

// ── Service ───────────────────────────────────────────────────────────────────

@Singleton
class FlightNumberService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: FlightNumberApiService
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("flight_number_cache", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val CACHE_TTL_MS = 24L * 60L * 60L * 1000L // 24 hours

    suspend fun lookupFlight(flightNumber: String): Result<FlightRoute> {
        val cacheKey = "flight_${flightNumber.uppercase()}"
        val cached = getCached(cacheKey)
        if (cached != null) return Result.success(cached)

        val today = dateFormat.format(Date())
        return try {
            val flights = apiService.getFlightByNumber(
                flightNumber = flightNumber.uppercase(),
                date = today
            )
            val flight = flights.firstOrNull()
                ?: return Result.failure(Exception("Flight $flightNumber not found"))

            val depIcao = flight.departure?.airport?.icao ?: ""
            val arrIcao = flight.arrival?.airport?.icao ?: ""
            val depCity = flight.departure?.airport?.municipalityName
                ?: flight.departure?.airport?.name
                ?: depIcao
            val arrCity = flight.arrival?.airport?.municipalityName
                ?: flight.arrival?.airport?.name
                ?: arrIcao
            val airlineName = flight.airline?.name ?: ""

            val route = FlightRoute(
                departureIcao = depIcao,
                arrivalIcao = arrIcao,
                departureCity = depCity,
                arrivalCity = arrCity,
                airline = airlineName,
                cachedAt = System.currentTimeMillis()
            )
            saveCache(cacheKey, route)
            Result.success(route)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getCached(key: String): FlightRoute? {
        val json = prefs.getString(key, null) ?: return null
        return try {
            val route = gson.fromJson(json, FlightRoute::class.java)
            if (System.currentTimeMillis() - route.cachedAt < CACHE_TTL_MS) route else null
        } catch (e: Exception) {
            null
        }
    }

    private fun saveCache(key: String, route: FlightRoute) {
        prefs.edit().putString(key, gson.toJson(route)).apply()
    }
}
