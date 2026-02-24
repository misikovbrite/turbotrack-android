package com.britetodo.turbotrack.ui.forecast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.britetodo.turbotrack.data.model.DailyForecast
import com.britetodo.turbotrack.data.model.TurbulenceForecast
import com.britetodo.turbotrack.data.model.TurbulenceSeverity
import com.britetodo.turbotrack.theme.TextMuted
import com.britetodo.turbotrack.theme.TextPrimary
import com.britetodo.turbotrack.theme.TextSecondary
import com.britetodo.turbotrack.theme.TurboBlue
import com.britetodo.turbotrack.theme.TurboCard
import com.britetodo.turbotrack.theme.TurboNavy
import com.britetodo.turbotrack.theme.TurboNavyMid
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ForecastStoryScreen(viewModel: RouteViewModel) {
    val state by viewModel.state.collectAsState()
    val forecast = state.forecastResult ?: return
    val pagerState = rememberPagerState(pageCount = { 4 })

    Column(modifier = Modifier.fillMaxSize()) {
        // Page indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(4) { page ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (page <= pagerState.currentPage) TurboBlue
                            else Color.White.copy(alpha = 0.2f)
                        )
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> StoryPage1Overall(forecast)
                1 -> StoryPage2DayByDay(forecast)
                2 -> StoryPage3Altitude(forecast)
                3 -> StoryPage4Map(forecast, onSeeFullReport = { viewModel.navigateToResult() })
            }
        }
    }
}

// ─── Page 1: Overall Severity Banner ─────────────────────────────────────────

@Composable
private fun StoryPage1Overall(forecast: TurbulenceForecast) {
    val severity = forecast.overallSeverity
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(severity.color.copy(alpha = 0.3f), TurboNavy)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(severity.color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (severity) {
                        TurbulenceSeverity.NONE -> "✓"
                        TurbulenceSeverity.LIGHT -> "~"
                        TurbulenceSeverity.MODERATE -> "⚡"
                        TurbulenceSeverity.SEVERE -> "⚠"
                        TurbulenceSeverity.EXTREME -> "☠"
                    },
                    fontSize = 40.sp
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = "${severity.displayName} Turbulence Expected",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "${forecast.origin.icao}  →  ${forecast.destination.icao}",
                fontSize = 16.sp,
                color = severity.color
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = severity.passengerAdvice,
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

// ─── Page 2: Day-by-day ───────────────────────────────────────────────────────

@Composable
private fun StoryPage2DayByDay(forecast: TurbulenceForecast) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Day by Day Outlook",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Turbulence forecast for the next ${forecast.days.size} days",
            fontSize = 14.sp,
            color = TextSecondary
        )
        Spacer(Modifier.height(24.dp))

        forecast.days.forEach { day ->
            DayForecastRow(day = day)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun DayForecastRow(day: DailyForecast) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TurboCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Text(
                    "${day.date.dayOfMonth} ${day.date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
            Box(
                modifier = Modifier
                    .background(day.severity.color.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(day.severity.displayName, color = day.severity.color, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─── Page 3: Altitude Bar Chart ───────────────────────────────────────────────

@Composable
private fun StoryPage3Altitude(forecast: TurbulenceForecast) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Altitude Breakdown",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Turbulence by flight level",
            fontSize = 14.sp,
            color = TextSecondary
        )
        Spacer(Modifier.height(24.dp))

        forecast.layers.forEach { layer ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "FL${layer.flightLevel}",
                    color = TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.width(50.dp)
                )
                Box(
                    modifier = Modifier
                        .weight((layer.windShear / 10.0).coerceIn(0.1, 1.0).toFloat())
                        .height(20.dp)
                        .background(layer.severity.color, RoundedCornerShape(4.dp))
                )
                Spacer(Modifier.weight(1f - (layer.windShear / 10.0).coerceIn(0.1, 1.0).toFloat()))
                Text(
                    layer.severity.shortName,
                    color = layer.severity.color,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

// ─── Page 4: Mini Map + See Full Report ──────────────────────────────────────

@Composable
private fun StoryPage4Map(forecast: TurbulenceForecast, onSeeFullReport: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Route Overview",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))

        // Route summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TurboCard)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(forecast.origin.icao, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(forecast.origin.city, color = TextMuted, fontSize = 12.sp)
                    }
                    Text("→", color = TurboBlue, fontSize = 20.sp)
                    Column(horizontalAlignment = Alignment.End) {
                        Text(forecast.destination.icao, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(forecast.destination.city, color = TextMuted, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Overall", color = TextMuted, fontSize = 11.sp)
                        Text(forecast.overallSeverity.displayName, color = forecast.overallSeverity.color, fontWeight = FontWeight.SemiBold)
                    }
                    Column {
                        Text("PIREPs", color = TextMuted, fontSize = 11.sp)
                        Text("${forecast.pirepCount}", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                    Column {
                        Text("Type", color = TextMuted, fontSize = 11.sp)
                        Text(forecast.primaryType.displayName.split(" ").first(), color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onSeeFullReport,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TurboBlue)
        ) {
            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(8.dp))
            Text("See Full Report", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.size(4.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}
