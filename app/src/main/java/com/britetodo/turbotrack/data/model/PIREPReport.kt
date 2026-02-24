package com.britetodo.turbotrack.data.model

import com.google.gson.annotations.SerializedName

data class PIREPReport(
    @SerializedName("airepType") val type: String? = null,
    @SerializedName("receiptTime") val receiptTime: String? = null,
    @SerializedName("obsTime") val obsTime: Long? = null,
    @SerializedName("mid") val id: String? = null,
    @SerializedName("acType") val aircraftType: String? = null,
    @SerializedName("icaoId") val icaoId: String? = null,
    @SerializedName("lat") val lat: Double? = null,
    @SerializedName("lon") val lon: Double? = null,
    @SerializedName("fltLvl") val flightLevel: Int? = null,
    @SerializedName("tbInt") val turbulenceIntensity: String? = null,
    @SerializedName("tbType") val turbulenceType: String? = null,
    @SerializedName("tbFreq") val turbulenceFreq: String? = null,
    @SerializedName("wdir") val windDirection: Int? = null,
    @SerializedName("wspd") val windSpeed: Int? = null,
    @SerializedName("temp") val temperature: Double? = null,
    @SerializedName("rawOb") val rawObservation: String? = null
) {
    val severity: TurbulenceSeverity
        get() = when (turbulenceIntensity?.uppercase()) {
            "NEG", "NONE", "NEG-LGT" -> TurbulenceSeverity.NONE
            "SMTH-LGT", "LGT", "LGT-MOD" -> TurbulenceSeverity.LIGHT
            "MOD", "MOD-SEV" -> TurbulenceSeverity.MODERATE
            "SEV", "SEV-EXTM" -> TurbulenceSeverity.SEVERE
            "EXTM" -> TurbulenceSeverity.EXTREME
            else -> TurbulenceSeverity.LIGHT
        }

    val displayFlightLevel: String
        get() = flightLevel?.let { "FL${it.toString().padStart(3, '0')}" } ?: "UNK"

    val displayAircraftType: String
        get() = aircraftType?.takeIf { it.isNotBlank() } ?: "Unknown Aircraft"

    val timeAgo: String
        get() {
            val obs = obsTime ?: return "Unknown"
            val diffMinutes = (System.currentTimeMillis() / 1000 - obs) / 60
            return when {
                diffMinutes < 60 -> "${diffMinutes}m ago"
                diffMinutes < 1440 -> "${diffMinutes / 60}h ago"
                else -> "${diffMinutes / 1440}d ago"
            }
        }
}
