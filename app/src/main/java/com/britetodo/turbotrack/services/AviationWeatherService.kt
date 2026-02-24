package com.britetodo.turbotrack.services

import com.britetodo.turbotrack.data.model.AirSigmet
import com.britetodo.turbotrack.data.model.PIREPReport
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AviationWeatherService {

    @GET("api/data/pirep")
    suspend fun getPireps(
        @Query("format") format: String = "json",
        @Query("age") age: Int = 2,
        @Query("bbox") bbox: String = "-140,15,-50,60"
    ): Response<List<PIREPReport>>

    @GET("api/data/isigmet")
    suspend fun getSigmets(
        @Query("format") format: String = "json",
        @Query("hazard") hazard: String = "turb"
    ): Response<List<AirSigmet>>
}
