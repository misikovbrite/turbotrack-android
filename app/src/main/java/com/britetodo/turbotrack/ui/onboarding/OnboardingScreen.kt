package com.britetodo.turbotrack.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.collectAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.britetodo.turbotrack.data.model.TurbulenceSeverity
import com.britetodo.turbotrack.theme.OrbBlue
import com.britetodo.turbotrack.theme.OrbCyan
import com.britetodo.turbotrack.theme.OrbPurple
import com.britetodo.turbotrack.theme.TextMuted
import com.britetodo.turbotrack.theme.TextPrimary
import com.britetodo.turbotrack.theme.TextSecondary
import com.britetodo.turbotrack.theme.TurboBlue
import com.britetodo.turbotrack.theme.TurboCard
import com.britetodo.turbotrack.theme.TurboNavy
import com.britetodo.turbotrack.theme.TurboNavyMid

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 13

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TurboNavy)
    ) {
        // Floating orb background
        FloatingOrbBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            // Progress indicator (steps 1+)
            if (currentStep > 0) {
                StepProgressBar(
                    currentStep = currentStep,
                    totalSteps = totalSteps,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }

            // Step content
            Box(modifier = Modifier.weight(1f)) {
                when (currentStep) {
                    0 -> Step0Welcome()
                    1 -> Step1CheckAnyRoute()
                    2 -> Step2ThreeDayForecast()
                    3 -> Step3EveryAltitude()
                    4 -> Step4PilotReports()
                    5 -> Step5DataSources()
                    else -> QuizAndBeyond(
                        step = currentStep,
                        viewModel = viewModel,
                        onComplete = onComplete
                    )
                }
            }

            // Next button (steps 0-5)
            if (currentStep <= 5) {
                Button(
                    onClick = { currentStep++ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TurboBlue)
                ) {
                    Text(
                        text = if (currentStep == 0) "Get Started" else "Next",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingOrbBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbY"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset((-80).dp, (-60 + offsetY).dp)
                .blur(80.dp)
                .background(OrbBlue, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(200.dp, (100 - offsetY * 0.5f).dp)
                .blur(60.dp)
                .background(OrbPurple, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(100.dp, (400 + offsetY * 0.3f).dp)
                .blur(50.dp)
                .background(OrbCyan, CircleShape)
        )
    }
}

@Composable
private fun StepProgressBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(totalSteps) { index ->
            val fraction = animateFloatAsState(
                targetValue = if (index < currentStep) 1f else 0f,
                animationSpec = tween(300),
                label = "progress$index"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction.value)
                        .height(3.dp)
                        .background(TurboBlue)
                )
            }
        }
    }
}

// ─── Step 0: Welcome ─────────────────────────────────────────────────────────

@Composable
private fun Step0Welcome() {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo with pulsing glow
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(glowScale)
                    .blur(40.dp)
                    .background(TurboBlue.copy(alpha = 0.5f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(TurboBlue, Color(0xFF0A3FA8))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FlightTakeoff,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(52.dp)
                )
            }
        }

        Spacer(Modifier.height(40.dp))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600)) + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TurboTrack",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Turbulence Forecast",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TurboBlue,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Know what to expect\nbefore you fly",
                    fontSize = 16.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

// ─── Step 1: Check Any Route ─────────────────────────────────────────────────

@Composable
private fun Step1CheckAnyRoute() {
    EntranceAnimatedStep {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FeatureStepHeader(
                icon = Icons.Default.FlightTakeoff,
                title = "Check Any Route",
                subtitle = "Get turbulence forecasts for any flight path worldwide"
            )
            Spacer(Modifier.height(40.dp))

            // Mock route card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = TurboCard)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("KJFK", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("New York", fontSize = 12.sp, color = TextMuted)
                        }
                        Icon(Icons.Default.Air, contentDescription = null, tint = TurboBlue, modifier = Modifier.size(28.dp))
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text("EGLL", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("London", fontSize = 12.sp, color = TextMuted)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tomorrow, 09:00", color = TextSecondary, fontSize = 13.sp)
                        SeverityBadge(TurbulenceSeverity.MODERATE)
                    }
                }
            }
        }
    }
}

// ─── Step 2: 3-Day Forecast ───────────────────────────────────────────────────

@Composable
private fun Step2ThreeDayForecast() {
    val days = listOf(
        Triple("Mon", TurbulenceSeverity.LIGHT, "Light"),
        Triple("Tue", TurbulenceSeverity.MODERATE, "Moderate"),
        Triple("Wed", TurbulenceSeverity.SEVERE, "Severe")
    )

    EntranceAnimatedStep {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FeatureStepHeader(
                icon = Icons.Default.Cloud,
                title = "3-Day Forecast",
                subtitle = "Plan ahead with multi-day turbulence forecasts"
            )
            Spacer(Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                days.forEachIndexed { index, (day, severity, label) ->
                    DayForecastCard(
                        day = day,
                        severity = severity,
                        label = label,
                        modifier = Modifier.weight(1f),
                        delayMs = index * 150
                    )
                }
            }
        }
    }
}

@Composable
private fun DayForecastCard(
    day: String,
    severity: TurbulenceSeverity,
    label: String,
    modifier: Modifier = Modifier,
    delayMs: Int = 0
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMs.toLong())
        visible = true
    }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "dayScale"
    )

    Card(
        modifier = modifier.scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = severity.color.copy(alpha = 0.15f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, severity.color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(day, color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(severity.color, CircleShape)
            )
            Spacer(Modifier.height(8.dp))
            Text(label, color = severity.color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ─── Step 3: Every Altitude ───────────────────────────────────────────────────

@Composable
private fun Step3EveryAltitude() {
    val flightLevels = listOf("FL100", "FL150", "FL200", "FL250", "FL300", "FL340", "FL390")
    val types = listOf("Wind Shear", "CAT", "SIGMET", "Convective", "Mountain Wave", "Jet Stream")

    EntranceAnimatedStep {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FeatureStepHeader(
                icon = Icons.Default.Layers,
                title = "Every Altitude",
                subtitle = "See turbulence at every flight level from 1,000ft to 45,000ft"
            )
            Spacer(Modifier.height(32.dp))

            // FL carousel
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                items(flightLevels) { fl ->
                    Chip(text = fl, color = TurboBlue)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Type carousel
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                items(types) { type ->
                    Chip(text = type, color = Color(0xFF7C3AED))
                }
            }
        }
    }
}

// ─── Step 4: Pilot Reports ────────────────────────────────────────────────────

@Composable
private fun Step4PilotReports() {
    data class MockPirep(val aircraft: String, val severity: TurbulenceSeverity, val fl: String, val ago: String)
    val pireps = listOf(
        MockPirep("B738", TurbulenceSeverity.MODERATE, "FL350", "12m ago"),
        MockPirep("A320", TurbulenceSeverity.LIGHT, "FL280", "28m ago"),
        MockPirep("B777", TurbulenceSeverity.SEVERE, "FL390", "1h ago")
    )

    EntranceAnimatedStep {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FeatureStepHeader(
                icon = Icons.Default.PinDrop,
                title = "Pilot Reports",
                subtitle = "Real-time PIREPs from actual pilots on your route"
            )
            Spacer(Modifier.height(32.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                pireps.forEachIndexed { index, pirep ->
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(index * 200L)
                        visible = true
                    }
                    val offsetY by animateFloatAsState(
                        targetValue = if (visible) 0f else 40f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "pirepOffset$index"
                    )
                    val alpha by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = tween(300),
                        label = "pirepAlpha$index"
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = offsetY.dp)
                            .then(Modifier.padding(0.dp).run { this }),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = TurboCard)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(pirep.severity.color.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pirep.severity.shortName.first().toString(),
                                    color = pirep.severity.color,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pirep.aircraft, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                Text("${pirep.severity.displayName} at ${pirep.fl}", color = TextSecondary, fontSize = 13.sp)
                            }
                            Text(pirep.ago, color = TextMuted, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ─── Step 5: Data Sources ─────────────────────────────────────────────────────

@Composable
private fun Step5DataSources() {
    data class DataSource(val name: String, val desc: String, val icon: ImageVector)
    val sources = listOf(
        DataSource("NOAA", "National Oceanic and Atmospheric Administration", Icons.Default.Public),
        DataSource("FAA AWC", "Aviation Weather Center", Icons.Default.FlightTakeoff),
        DataSource("Open-Meteo", "Global weather model forecasts", Icons.Default.Cloud),
        DataSource("PIREPs", "Real-time pilot reports", Icons.Default.PinDrop)
    )

    EntranceAnimatedStep {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FeatureStepHeader(
                icon = Icons.Default.Star,
                title = "Trusted Data Sources",
                subtitle = "Powered by official aviation and meteorological data"
            )
            Spacer(Modifier.height(32.dp))

            val rows = sources.chunked(2)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { source ->
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = TurboCard)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Icon(
                                        imageVector = source.icon,
                                        contentDescription = null,
                                        tint = TurboBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(source.name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Text(source.desc, color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
                                }
                            }
                        }
                        // Fill empty slot if odd count
                        if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// ─── Shared Composables ───────────────────────────────────────────────────────

@Composable
private fun EntranceAnimatedStep(content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + slideInVertically(
            initialOffsetY = { it / 6 },
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )
    ) {
        content()
    }
}

@Composable
private fun FeatureStepHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(TurboBlue.copy(alpha = 0.15f), CircleShape)
                .border(1.dp, TurboBlue.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = TurboBlue, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            fontSize = 15.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun SeverityBadge(severity: TurbulenceSeverity, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(severity.color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .border(1.dp, severity.color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = severity.displayName,
            color = severity.color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─── QuizAndBeyond: Steps 6–12 (implemented in OnboardingQuizScreen.kt) ──────

@Composable
fun QuizAndBeyond(
    step: Int,
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    OnboardingQuizContent(
        step = step,
        state = state,
        onQ1 = viewModel::setQ1,
        onQ2 = viewModel::setQ2,
        onQ3 = viewModel::setQ3,
        onQ4Toggle = viewModel::toggleQ4,
        onQ5 = viewModel::setQ5,
        onComplete = {
            viewModel.completeOnboarding()
            onComplete()
        }
    )
}

@Composable
private fun Chip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = color, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
