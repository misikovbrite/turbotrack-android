package com.britetodo.turbotrack.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary           = Color(0xFF007AFF),
    onPrimary         = Color.White,
    primaryContainer  = Color(0xFFD1E4FF),
    secondary         = Color(0xFF6E6E73),
    onSecondary       = Color.White,
    background        = Color(0xFFF2F2F7),
    onBackground      = Color(0xFF1C1C1E),
    surface           = Color(0xFFFFFFFF),
    onSurface         = Color(0xFF1C1C1E),
    surfaceVariant    = Color(0xFFF2F2F7),
    onSurfaceVariant  = Color(0xFF6E6E73),
    outline           = Color(0xFFC6C6C8),
    error             = Color(0xFFFF3B30),
    onError           = Color.White,
)

@Composable
fun TurboTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
