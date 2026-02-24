package com.britetodo.turbotrack.ui.forecast

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.britetodo.turbotrack.data.model.TurbulenceForecast
import com.britetodo.turbotrack.data.model.TurbulenceSeverity
import com.britetodo.turbotrack.theme.TextMuted
import com.britetodo.turbotrack.theme.TextPrimary
import com.britetodo.turbotrack.theme.TextSecondary
import com.britetodo.turbotrack.theme.TurboBackground
import com.britetodo.turbotrack.theme.TurboBlue
import com.britetodo.turbotrack.theme.TurboCard
import java.time.format.TextStyle
import java.util.Locale

// ─── Color constants (matching iOS exactly) ───────────────────────────────────
private val PageIndicatorSelected = TurboBlue
private val PageIndicatorUnselected = Color(0xFF8E8E93).copy(alpha = 0.30f)

// ─── Root Screen ─────────────────────────────────────────────────────────────

@Composable
fun ForecastStoryScreen(viewModel: RouteViewModel) {
    val state by viewModel.state.collectAsState()
    val forecast = state.forecastResult ?: return
    val pagerState = rememberPagerState(pageCount = { 4 })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TurboBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top bar: page indicators + Skip ──────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page indicator capsules
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(4) { index ->
                        val isSelected = index == pagerState.currentPage
                        val targetWidth = if (isSelected) 24.dp else 8.dp
                        val animatedWidth by animateDpAsState(
                            targetValue = targetWidth,
                            animationSpec = tween(durationMillis = 250),
                            label = "indicator_width_$index"
                        )
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .width(animatedWidth)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isSelected) PageIndicatorSelected
                                    else PageIndicatorUnselected
                                )
                        )
                    }
                }

                // Skip button (top-right)
                TextButton(onClick = { viewModel.navigateToResult() }) {
                    Text(
                        text = "Skip",
                        color = TextSecondary,
                        fontSize = 15.sp
                    )
                }
            }

            // ── HorizontalPager ──────────────────────────────────────────────
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> StoryPage1Flight(forecast)
                    1 -> StoryPage2Turbulence(forecast)
                    2 -> StoryPage3RouteProfile(forecast)
                    3 -> StoryPage4SafetyTips(forecast)
                }
            }

            // ── Bottom: View Full Report button ──────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateToResult() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TurboBlue)
                ) {
                    Text(
                        text = "View Full Report",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ─── Page 1 – Your Flight ────────────────────────────────────────────────────

@Composable
private fun StoryPage1Flight(forecast: TurbulenceForecast) {
    val depCity = forecast.origin.city
    val arrCity = forecast.destination.city
    val routeDisplay = "$depCity → $arrCity"
    val routeIcao = "${forecast.origin.icao} → ${forecast.destination.icao}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Blue airplane icon 60sp
        Icon(
            imageVector = Icons.Default.FlightTakeoff,
            contentDescription = "Flight",
            tint = TurboBlue,
            modifier = Modifier.size(60.dp)
        )

        Spacer(Modifier.height(24.dp))

        // "YOUR FLIGHT" caption
        Text(
            text = "YOUR FLIGHT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        // Route display name bold title
        Text(
            text = routeDisplay,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(6.dp))

        // ICAO subtitle secondary
        Text(
            text = routeIcao,
            fontSize = 15.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))

        // Forecast horizon chip
        Box(
            modifier = Modifier
                .background(
                    color = TurboBlue.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${forecast.days.size}-day forecast",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TurboBlue
            )
        }
    }
}

// ─── Page 2 – Turbulence Level ───────────────────────────────────────────────

@Composable
private fun StoryPage2Turbulence(forecast: TurbulenceForecast) {
    val severity = forecast.overallSeverity

    val severityIcon: ImageVector = when (severity) {
        TurbulenceSeverity.NONE -> Icons.Default.CheckCircle
        TurbulenceSeverity.LIGHT -> Icons.Default.Cloud
        TurbulenceSeverity.MODERATE -> Icons.Default.Air
        TurbulenceSeverity.SEVERE, TurbulenceSeverity.EXTREME -> Icons.Default.Warning
    }

    // advice.title and brief description come from severity fields
    val adviceTitle: String = when (severity) {
        TurbulenceSeverity.NONE -> "Smooth Skies Ahead"
        TurbulenceSeverity.LIGHT -> "Expect Light Bumps"
        TurbulenceSeverity.MODERATE -> "Moderate Turbulence"
        TurbulenceSeverity.SEVERE -> "Severe Turbulence"
        TurbulenceSeverity.EXTREME -> "Extreme Turbulence"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Concentric circles: outer 140dp (15%), middle 100dp (30%), inner icon 44dp
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(140.dp)
        ) {
            // Outer ring (15% opacity)
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(50))
                    .background(severity.color.copy(alpha = 0.15f))
            )
            // Middle ring (30% opacity)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50))
                    .background(severity.color.copy(alpha = 0.30f))
            )
            // Inner icon box
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = severityIcon,
                    contentDescription = severity.displayName,
                    tint = severity.color,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // advice.title (title2.bold equivalent: 22sp bold)
        Text(
            text = adviceTitle,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        // Severity name capsule badge
        Box(
            modifier = Modifier
                .background(
                    color = severity.color.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = severity.displayName,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = severity.color
            )
        }

        Spacer(Modifier.height(16.dp))

        // Brief description (secondary, center)
        Text(
            text = severity.passengerAdvice,
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

// ─── Page 3 – Route Profile ──────────────────────────────────────────────────

@Composable
private fun StoryPage3RouteProfile(forecast: TurbulenceForecast) {
    // Build profile segments from altitude layers ordered by flight level
    val profileSegments: List<TurbulenceSeverity> = if (forecast.layers.isNotEmpty()) {
        forecast.layers.sortedBy { it.flightLevel }.map { it.severity }
    } else {
        // Fallback: use overall severity repeated
        List(6) { forecast.overallSeverity }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // chart.line equivalent icon
        Icon(
            imageVector = Icons.Default.Layers,
            contentDescription = "Route Profile",
            tint = TurboBlue,
            modifier = Modifier.size(40.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Route Profile",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))

        // White card with airport labels + turbulence bar + legend
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TurboCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Airport code labels: DEP left, ARR right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = forecast.origin.icao,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Text(
                        text = forecast.destination.icao,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Turbulence bar: horizontal colored segments
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    profileSegments.forEachIndexed { index, seg ->
                        val isFirst = index == 0
                        val isLast = index == profileSegments.lastIndex
                        val segShape = when {
                            isFirst && isLast -> RoundedCornerShape(6.dp)
                            isFirst -> RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp, topEnd = 0.dp, bottomEnd = 0.dp)
                            isLast -> RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp, topStart = 0.dp, bottomStart = 0.dp)
                            else -> RoundedCornerShape(0.dp)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(color = seg.color, shape = segShape)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Legend: Smooth / Light / Moderate / Severe
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf(
                        TurbulenceSeverity.NONE,
                        TurbulenceSeverity.LIGHT,
                        TurbulenceSeverity.MODERATE,
                        TurbulenceSeverity.SEVERE
                    ).forEach { sev ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(sev.color)
                            )
                            Text(
                                text = sev.displayName,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Daily forecast card (3 days): day name left, severity dot + name right
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TurboCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                forecast.days.forEachIndexed { index, day ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = day.date.dayOfWeek.getDisplayName(
                                TextStyle.FULL, Locale.getDefault()
                            ),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(day.severity.color)
                            )
                            Text(
                                text = day.severity.displayName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = day.severity.color
                            )
                        }
                    }
                    if (index < forecast.days.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.5.dp)
                                .background(Color(0xFFC6C6C8))
                        )
                    }
                }
            }
        }
    }
}

// ─── Page 4 – Safety Tips ────────────────────────────────────────────────────

private data class SafetyTip(
    val icon: ImageVector,
    val title: String,
    val detail: String
)

private fun safetyTipsFor(severity: TurbulenceSeverity): List<SafetyTip> = when (severity) {
    TurbulenceSeverity.NONE -> listOf(
        SafetyTip(
            Icons.Default.CheckCircle,
            "Smooth Conditions",
            "No turbulence expected. Enjoy your flight comfortably."
        ),
        SafetyTip(
            Icons.Default.Star,
            "Stay Comfortable",
            "Feel free to move around and use cabin services normally."
        ),
        SafetyTip(
            Icons.Default.Info,
            "Stay Aware",
            "Conditions can change. Keep your seatbelt fastened when seated."
        )
    )
    TurbulenceSeverity.LIGHT -> listOf(
        SafetyTip(
            Icons.Default.Notifications,
            "Fasten Seatbelt",
            "Keep your seatbelt loosely fastened whenever you are seated."
        ),
        SafetyTip(
            Icons.Default.Cloud,
            "Minor Bumpiness",
            "Slight bumpiness may occur but will be brief and manageable."
        ),
        SafetyTip(
            Icons.Default.Info,
            "Secure Loose Items",
            "Stow small items in seat pockets or overhead bins."
        )
    )
    TurbulenceSeverity.MODERATE -> listOf(
        SafetyTip(
            Icons.Default.Warning,
            "Fasten Seatbelt Firmly",
            "Keep your seatbelt tightly fastened throughout the flight."
        ),
        SafetyTip(
            Icons.Default.Air,
            "Expect Noticeable Bumps",
            "Unsecured items may shift. Secure everything in your area."
        ),
        SafetyTip(
            Icons.Default.Notifications,
            "Limited Cabin Service",
            "Crew may suspend service during turbulent periods for safety."
        ),
        SafetyTip(
            Icons.Default.Info,
            "Follow Crew Instructions",
            "Listen to and immediately follow all crew announcements."
        )
    )
    TurbulenceSeverity.SEVERE, TurbulenceSeverity.EXTREME -> listOf(
        SafetyTip(
            Icons.Default.Warning,
            "Seatbelt Tightly Fastened",
            "Remain seated with your seatbelt as tight as possible at all times."
        ),
        SafetyTip(
            Icons.Default.Notifications,
            "Obey All Crew Instructions",
            "Follow crew directives immediately without hesitation."
        ),
        SafetyTip(
            Icons.Default.Air,
            "Brace for Strong Forces",
            "Hold on to arm rests. Aircraft may experience sudden altitude changes."
        ),
        SafetyTip(
            Icons.Default.Info,
            "Stay Calm",
            "Modern aircraft are built to withstand severe turbulence safely."
        )
    )
}

@Composable
private fun StoryPage4SafetyTips(forecast: TurbulenceForecast) {
    val tips = safetyTipsFor(forecast.overallSeverity)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // shield.check equivalent
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Safety Tips",
            tint = TurboBlue,
            modifier = Modifier.size(44.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Safety Tips",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Based on your forecast",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))

        // White card with tips
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TurboCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                tips.forEachIndexed { index, tip ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Icon 16sp blue, fixed width 24dp
                        Box(
                            modifier = Modifier.width(24.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Icon(
                                imageVector = tip.icon,
                                contentDescription = null,
                                tint = TurboBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tip.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = tip.detail,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                    if (index < tips.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 52.dp)
                                .height(0.5.dp)
                                .background(Color(0xFFC6C6C8))
                        )
                    }
                }
            }
        }
    }
}
