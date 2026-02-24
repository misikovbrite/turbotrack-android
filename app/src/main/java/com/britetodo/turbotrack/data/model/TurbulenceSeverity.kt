package com.britetodo.turbotrack.data.model

import androidx.compose.ui.graphics.Color
import com.britetodo.turbotrack.theme.SeverityExtreme
import com.britetodo.turbotrack.theme.SeverityLight
import com.britetodo.turbotrack.theme.SeverityModerate
import com.britetodo.turbotrack.theme.SeverityNone
import com.britetodo.turbotrack.theme.SeveritySevere

enum class TurbulenceSeverity(
    val displayName: String,
    val shortName: String,
    val color: Color,
    val description: String,
    val passengerAdvice: String
) {
    NONE(
        displayName = "Smooth",
        shortName = "Smooth",
        color = SeverityNone,
        description = "No turbulence expected along this route.",
        passengerAdvice = "Expect a very comfortable flight. No special precautions needed."
    ),
    LIGHT(
        displayName = "Light",
        shortName = "Light",
        color = SeverityLight,
        description = "Light turbulence may cause slight bumpiness.",
        passengerAdvice = "Minor bumpiness may occur. Keep seatbelt fastened when seated as a precaution."
    ),
    MODERATE(
        displayName = "Moderate",
        shortName = "Moderate",
        color = SeverityModerate,
        description = "Moderate turbulence may cause unsecured objects to move.",
        passengerAdvice = "Noticeable bumpiness expected. Keep seatbelt fastened and secure loose items. Cabin service may be limited."
    ),
    SEVERE(
        displayName = "Severe",
        shortName = "Severe",
        color = SeveritySevere,
        description = "Severe turbulence. Aircraft may be momentarily out of control.",
        passengerAdvice = "Strong turbulence expected. Ensure seatbelt is tightly fastened. Follow all crew instructions immediately."
    ),
    EXTREME(
        displayName = "Extreme",
        shortName = "Extreme",
        color = SeverityExtreme,
        description = "Extreme turbulence. Aircraft is tossed violently.",
        passengerAdvice = "Extreme caution. Remain seated with seatbelt tightly fastened at all times. Follow all crew instructions."
    );

    companion object {
        fun fromWindShear(shear: Double): TurbulenceSeverity = when {
            shear >= 8.0 -> EXTREME
            shear >= 6.0 -> SEVERE
            shear >= 4.0 -> MODERATE
            shear >= 2.0 -> LIGHT
            else -> NONE
        }
    }
}
