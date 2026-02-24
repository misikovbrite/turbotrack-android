package com.britetodo.turbotrack.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TurboBlue,
    onPrimary = Color.White,
    primaryContainer = TurboBlueDark,
    onPrimaryContainer = Color.White,
    secondary = TurboBlueLight,
    onSecondary = Color.White,
    background = TurboNavy,
    onBackground = TextPrimary,
    surface = TurboNavyMid,
    onSurface = TextPrimary,
    surfaceVariant = TurboCard,
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF2A3550),
    error = ColorError,
    onError = Color.White
)

@Composable
fun TurboTrackTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
