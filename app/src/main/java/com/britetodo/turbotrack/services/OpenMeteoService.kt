package com.britetodo.turbotrack.services

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoService {

    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "wind_speed_1000hPa,wind_direction_1000hPa," +
                "wind_speed_925hPa,wind_direction_925hPa," +
                "wind_speed_850hPa,wind_direction_850hPa," +
                "wind_speed_700hPa,wind_direction_700hPa," +
                "wind_speed_500hPa,wind_direction_500hPa," +
                "wind_speed_400hPa,wind_direction_400hPa," +
                "wind_speed_300hPa,wind_direction_300hPa," +
                "wind_speed_250hPa,wind_direction_250hPa," +
                "wind_speed_200hPa,wind_direction_200hPa",
        @Query("forecast_days") forecastDays: Int = 3,
        @Query("wind_speed_unit") windSpeedUnit: String = "kn",
        @Query("timezone") timezone: String = "UTC"
    ): JsonObject
}
