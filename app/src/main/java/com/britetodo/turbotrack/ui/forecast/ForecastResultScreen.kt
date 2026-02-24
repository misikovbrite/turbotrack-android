package com.britetodo.turbotrack.ui.forecast

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.britetodo.turbotrack.data.model.TurbulenceForecast
import com.britetodo.turbotrack.data.model.TurbulenceSeverity
import com.britetodo.turbotrack.theme.TextMuted
import com.britetodo.turbotrack.theme.TextPrimary
import com.britetodo.turbotrack.theme.TextSecondary
import com.britetodo.turbotrack.theme.TurboBlue
import com.britetodo.turbotrack.theme.TurboCard
import com.britetodo.turbotrack.theme.TurboNavy
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastResultScreen(viewModel: RouteViewModel) {
    val state by viewModel.state.collectAsState()
    val forecast = state.forecastResult ?: return
    val context = LocalContext.current
    var showGuide by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(TurboNavy)) {
        // Top bar
        TopAppBar(
            title = { Text("Forecast Result", color = TextPrimary) },
            navigationIcon = {
                IconButton(onClick = { viewModel.navigateToInput() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
            },
            actions = {
                IconButton(onClick = {
                    val shareText = buildShareText(forecast)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share Forecast"))
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = TurboBlue)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = TurboNavy)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Status banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(forecast.overallSeverity.color.copy(alpha = 0.15f))
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(forecast.overallSeverity.color.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(forecast.overallSeverity.color, CircleShape)
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                "${forecast.overallSeverity.displayName} Turbulence",
                                color = forecast.overallSeverity.color,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text(
                                "${forecast.origin.icao} → ${forecast.destination.icao}",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Passenger advisory
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TurboCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Passenger Advisory", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        Text(forecast.overallSeverity.passengerAdvice, color = TextSecondary, fontSize = 14.sp, lineHeight = 22.sp)
                    }
                }
            }

            // Daily forecast cards
            item {
                Text(
                    "Day by Day",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
                ) {
                    items(forecast.days) { day ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = TurboCard)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp).width(90.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(day.severity.color, CircleShape)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(day.severity.shortName, color = day.severity.color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // FL breakdown
            item {
                Text(
                    "Altitude Breakdown",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(forecast.layers) { layer ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(TurboCard, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("FL${layer.flightLevel}", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.width(55.dp))
                    Text("${layer.altitudeFt / 1000}k ft", color = TextMuted, fontSize = 11.sp, modifier = Modifier.width(55.dp))
                    Spacer(Modifier.weight(1f))
                    Text("%.1f kt/1000ft".format(layer.windShear), color = TextMuted, fontSize = 12.sp)
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(layer.severity.color.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(layer.severity.shortName, color = layer.severity.color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // PIREP summary
            item {
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TurboCard)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Pilot Reports Near Route", color = TextPrimary, modifier = Modifier.weight(1f))
                        Text("${forecast.pirepCount}", color = TurboBlue, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }

            // Understanding Turbulence guide
            item {
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { showGuide = !showGuide },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TurboCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Understanding Turbulence Levels", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            Icon(
                                if (showGuide) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                        AnimatedVisibility(visible = showGuide, enter = expandVertically(), exit = shrinkVertically()) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                TurbulenceSeverity.entries.forEach { sev ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(modifier = Modifier.size(10.dp).background(sev.color, CircleShape).padding(top = 4.dp))
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Text(sev.displayName, color = sev.color, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                            Text(sev.description, color = TextMuted, fontSize = 12.sp, lineHeight = 18.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Disclaimer
            item {
                Text(
                    text = "Turbulence forecasts are based on atmospheric models and pilot reports. Always consult official aviation weather services and follow crew instructions. This app is for informational purposes only.",
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

private fun buildShareText(forecast: TurbulenceForecast): String {
    return buildString {
        appendLine("✈️ TurboTrack Forecast")
        appendLine("${forecast.origin.icao} → ${forecast.destination.icao}")
        appendLine("(${forecast.origin.city} to ${forecast.destination.city})")
        appendLine()
        appendLine("Overall: ${forecast.overallSeverity.displayName} Turbulence")
        appendLine()
        forecast.days.forEach { day ->
            appendLine("${day.date}: ${day.severity.displayName}")
        }
        appendLine()
        appendLine(forecast.overallSeverity.passengerAdvice)
        appendLine()
        appendLine("Forecast by TurboTrack")
    }
}
