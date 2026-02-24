package com.britetodo.turbotrack.data.model

enum class TurbulenceType(
    val displayName: String,
    val icon: String,
    val description: String
) {
    CAT(
        displayName = "Clear Air Turbulence",
        icon = "🌪",
        description = "High-altitude turbulence not associated with clouds, caused by jet streams and wind shear."
    ),
    CONVECTIVE(
        displayName = "Convective",
        icon = "⛈",
        description = "Turbulence associated with thunderstorms and convective activity."
    ),
    MOUNTAIN_WAVE(
        displayName = "Mountain Wave",
        icon = "🏔",
        description = "Turbulence caused by airflow over mountain ranges creating wave patterns."
    ),
    COMBINED(
        displayName = "Combined",
        icon = "⚡",
        description = "Multiple turbulence types present simultaneously."
    )
}
