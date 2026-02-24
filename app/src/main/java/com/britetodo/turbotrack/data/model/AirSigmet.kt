package com.britetodo.turbotrack.data.model

import com.google.gson.annotations.SerializedName

data class AirSigmet(
    @SerializedName("icaoId") val icaoId: String? = null,
    @SerializedName("firId") val firId: String? = null,
    @SerializedName("sigmetId") val sigmetId: String? = null,
    @SerializedName("hazard") val hazard: String? = null,
    @SerializedName("qualifier") val qualifier: String? = null,
    @SerializedName("validTimeFrom") val validTimeFrom: Long? = null,
    @SerializedName("validTimeTo") val validTimeTo: Long? = null,
    @SerializedName("altitudeLow1") val altitudeLow1: Int? = null,
    @SerializedName("altitudeLow2") val altitudeLow2: Int? = null,
    @SerializedName("altitudeHi1") val altitudeHi1: Int? = null,
    @SerializedName("altitudeHi2") val altitudeHi2: Int? = null,
    @SerializedName("rawAirSigmet") val rawText: String? = null,
    @SerializedName("coords") val coords: List<SigmetCoord>? = null
) {
    val isActive: Boolean
        get() {
            val now = System.currentTimeMillis() / 1000
            val from = validTimeFrom ?: return false
            val to = validTimeTo ?: return false
            return now in from..to
        }

    val altitudeRange: String
        get() {
            val lo = altitudeLow1?.let { "FL${(it / 100).toString().padStart(3, '0')}" } ?: "SFC"
            val hi = altitudeHi1?.let { "FL${(it / 100).toString().padStart(3, '0')}" } ?: "UNL"
            return "$lo–$hi"
        }
}

data class SigmetCoord(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
)
