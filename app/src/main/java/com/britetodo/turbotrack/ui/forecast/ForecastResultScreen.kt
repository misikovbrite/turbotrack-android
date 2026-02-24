package com.britetodo.turbotrack.ui.forecast

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.britetodo.turbotrack.data.model.DailyForecast
import com.britetodo.turbotrack.data.model.TurbulenceSeverity
import com.britetodo.turbotrack.theme.SeverityLight
import com.britetodo.turbotrack.theme.SeverityModerate
import com.britetodo.turbotrack.theme.SeverityNone
import com.britetodo.turbotrack.theme.SeveritySevere
import com.britetodo.turbotrack.theme.TextMuted
import com.britetodo.turbotrack.theme.TextPrimary
import com.britetodo.turbotrack.theme.TextSecondary
import com.britetodo.turbotrack.theme.TurboBackground
import com.britetodo.turbotrack.theme.TurboBlue
import com.britetodo.turbotrack.theme.TurboCard
import java.time.format.TextStyle
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// Constants
// ─────────────────────────────────────────────────────────────────────────────
private val CardCorner      = 14.dp
private val CardElevation   = 1.dp
private val HorizontalPad   = 16.dp
private val SectionGap      = 8.dp

// ─────────────────────────────────────────────────────────────────────────────
// Root screen
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastResultScreen(viewModel: RouteViewModel) {
    val state by viewModel.state.collectAsState()
    val forecast = state.forecastResult ?: return
    val context  = LocalContext.current

    val advice       = viewModel.forecastAdvice
    val adviceColor  = Color(advice.color)        // Long → Compose Color
    val routeTitle   = viewModel.routeTitle
    val horizonText  = viewModel.forecastHorizonText
    val segments     = viewModel.leg1ProfileSegments
    val dailyList    = viewModel.dailyForecast
    val flLevels     = viewModel.flightLevelBreakdown
    val pirepText    = viewModel.pirepSummary
    val depCode      = forecast.origin.icao
    val arrCode      = forecast.destination.icao

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TurboBackground)
    ) {
        // ── Top App Bar ──────────────────────────────────────────────────────
        TopAppBar(
            title = {
                Text(
                    text = routeTitle,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                )
            },
            navigationIcon = {
                TextButton(onClick = { viewModel.clearRoute() }) {
                    Text(
                        text = "← New Search",
                        color = TurboBlue,
                        fontSize = 15.sp
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    val text = buildShareText(viewModel)
                    context.startActivity(
                        Intent.createChooser(
                            Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, text)
                            },
                            "Share Forecast"
                        )
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.IosShare,
                        contentDescription = "Share",
                        tint = TurboBlue
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = TurboBackground)
        )

        // ── Scrollable body ──────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(SectionGap)
        ) {

            // 1. Status banner
            item {
                StatusBanner(
                    advice = advice,
                    adviceColor = adviceColor,
                    horizonText = horizonText
                )
            }

            // 2. Forecast period picker
            item {
                ForecastPeriodPicker(
                    selectedDays = state.forecastDays,
                    onSelect = { days ->
                        viewModel.setForecastDays(days)
                        viewModel.checkTurbulence()
                    }
                )
            }

            // 3. Passenger advisory
            item { AdviceCard(detail = advice.detail) }

            // 4. Route turbulence profile bar
            item {
                RouteProfileSection(
                    depCode = depCode,
                    arrCode = arrCode,
                    segments = segments
                )
            }

            // 5. Daily forecast (only if non-empty)
            if (dailyList.isNotEmpty()) {
                item { DailyForecastSection(days = dailyList) }
            }

            // 6. By flight level (only if non-empty)
            if (flLevels.isNotEmpty()) {
                item { FlightLevelSection(entries = flLevels) }
            }

            // 7. PIREPs
            item { PirepSection(summary = pirepText) }

            // 8. Turbulence guide
            item { TurbulenceGuideSection() }

            // 9. Share button
            item {
                Button(
                    onClick = {
                        val text = buildShareText(viewModel)
                        context.startActivity(
                            Intent.createChooser(
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, text)
                                },
                                "Share Forecast"
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HorizontalPad)
                        .height(50.dp),
                    shape = RoundedCornerShape(CardCorner),
                    colors = ButtonDefaults.buttonColors(containerColor = TurboBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.IosShare,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Share Report",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }

            // 10. Disclaimer
            item {
                Text(
                    text = "Turbulence forecasts are based on atmospheric models and pilot reports. " +
                           "Always consult official aviation weather services and follow crew instructions. " +
                           "This app is for informational purposes only.",
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. Status banner
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun StatusBanner(
    advice: ForecastAdvice,
    adviceColor: Color,
    horizonText: String
) {
    // Map iconName from ViewModel to Material icon
    val icon: ImageVector = when (advice.iconName) {
        "check_circle" -> Icons.Default.CheckCircle
        "cloud"        -> Icons.Default.Cloud
        "bolt"         -> Icons.Default.Bolt
        else           -> Icons.Default.Warning   // "warning" and any unknown
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalPad)
            .clip(RoundedCornerShape(CardCorner))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(adviceColor, adviceColor.copy(alpha = 0.72f))
                )
            )
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    text = advice.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 24.sp
                )
                Text(
                    text = horizonText,
                    color = Color.White.copy(alpha = 0.80f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. Forecast period picker
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ForecastPeriodPicker(selectedDays: Int, onSelect: (Int) -> Unit) {
    SectionCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Forecast Period",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(3, 7, 14).forEach { days ->
                val selected = selectedDays == days
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) TurboBlue else Color(0xFFE5E5EA))
                        .clickable { onSelect(days) }
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${days}d",
                        color = if (selected) Color.White else TextSecondary,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. Passenger advisory
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AdviceCard(detail: String) {
    SectionCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Passenger Advisory",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
        Text(
            text = detail,
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. Route turbulence profile bar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RouteProfileSection(
    depCode: String,
    arrCode: String,
    segments: List<TurbulenceSeverity>
) {
    SectionCard {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AirplanemodeActive,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Route Turbulence Profile",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
        Text(
            text = "Turbulence intensity along your flight path",
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        // Airport labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = depCode,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
            Text(
                text = arrCode,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }

        // 15-segment turbulence bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            segments.forEach { sev ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(sev.color)
                )
            }
        }

        // Legend
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(
                "Smooth"   to SeverityNone,
                "Light"    to SeverityLight,
                "Moderate" to SeverityModerate,
                "Severe"   to SeveritySevere
            ).forEach { (label, color) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(text = label, color = TextMuted, fontSize = 11.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. Daily forecast
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DailyForecastSection(days: List<DailyForecast>) {
    SectionCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Daily Forecast",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }

        days.forEachIndexed { index, day ->
            if (index > 0) {
                Divider(color = Color(0xFFE5E5EA), thickness = 0.5.dp)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    color = TextPrimary,
                    fontSize = 15.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(day.severity.color)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = day.severity.displayName,
                        color = day.severity.color,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. By flight level
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FlightLevelSection(entries: List<FlightLevelInfo>) {
    SectionCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SwapVert,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "By Flight Level",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
        Text(
            text = "Detailed altitude analysis",
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        entries.forEachIndexed { index, entry ->
            if (index > 0) {
                Divider(color = Color(0xFFE5E5EA), thickness = 0.5.dp)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // FL code – monospaced bold
                Text(
                    text = "FL${entry.level}",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    modifier = Modifier.width(56.dp)
                )
                // Altitude in ft (FL * 100)
                Text(
                    text = "${entry.level * 100} ft",
                    color = TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.width(70.dp)
                )
                // Severity badge — coloured capsule
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(entry.severity.color.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = entry.severity.displayName,
                        color = entry.severity.color,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
                Spacer(Modifier.weight(1f))
                // Wind shear value
                Text(
                    text = "Shear ${"%.1f".format(entry.avgShear)} kt/kft",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 7. PIREPs
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PirepSection(summary: String) {
    SectionCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ModeComment,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Pilot Reports (PIREPs)",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
        Text(
            text = summary,
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 8. Turbulence guide
// ─────────────────────────────────────────────────────────────────────────────
private data class GuideRow(val label: String, val description: String, val color: Color)

private val guideRows = listOf(
    GuideRow(
        label = "Smooth",
        description = "No turbulence. Conditions are smooth and comfortable.",
        color = SeverityNone
    ),
    GuideRow(
        label = "Light",
        description = "Slight bumpiness. Drink spills possible. Seatbelt recommended.",
        color = SeverityLight
    ),
    GuideRow(
        label = "Moderate",
        description = "Definite bumpiness. Unsecured objects may move. Stay seated.",
        color = SeverityModerate
    ),
    GuideRow(
        label = "Severe",
        description = "Large abrupt changes. Aircraft may be briefly out of control.",
        color = SeveritySevere
    )
)

@Composable
private fun TurbulenceGuideSection() {
    SectionCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Understanding Turbulence Levels",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }

        guideRows.forEachIndexed { index, row ->
            if (index > 0) Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(row.color)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = row.label,
                        color = row.color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = row.description,
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable white card wrapper
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalPad),
        shape = RoundedCornerShape(CardCorner),
        colors = CardDefaults.cardColors(containerColor = TurboCard),
        elevation = CardDefaults.cardElevation(defaultElevation = CardElevation)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Share text builder
// ─────────────────────────────────────────────────────────────────────────────
private fun buildShareText(viewModel: RouteViewModel): String {
    val forecast = viewModel.state.value.forecastResult
    return buildString {
        appendLine("TurboTrack Turbulence Forecast")
        appendLine(viewModel.routeTitle)
        if (forecast != null) {
            appendLine("${forecast.origin.city} to ${forecast.destination.city}")
        }
        appendLine()
        appendLine("Overall: ${viewModel.forecastAdvice.title}")
        appendLine(viewModel.forecastHorizonText)
        appendLine()
        appendLine(viewModel.forecastAdvice.detail)
        appendLine()
        viewModel.dailyForecast.forEach { day ->
            val dayName = day.date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            appendLine("$dayName: ${day.severity.displayName}")
        }
        appendLine()
        appendLine(viewModel.pirepSummary)
        appendLine()
        appendLine("Forecast by TurboTrack")
    }
}
