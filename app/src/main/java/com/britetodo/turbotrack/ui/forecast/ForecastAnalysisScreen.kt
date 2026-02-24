package com.britetodo.turbotrack.ui.forecast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ── Constants ────────────────────────────────────────────────────────────────

private const val TOTAL_SECONDS = 75

private data class AnalysisStep(val triggerAt: Int, val label: String)

private val ANALYSIS_STEPS = listOf(
    AnalysisStep(0,  "Connecting to aviation data sources..."),
    AnalysisStep(10, "Analyzing flight route & altitude..."),
    AnalysisStep(20, "Checking atmospheric pressure levels..."),
    AnalysisStep(32, "Fetching live pilot reports..."),
    AnalysisStep(44, "Processing wind shear data..."),
    AnalysisStep(56, "Generating your forecast..."),
    AnalysisStep(66, "Finalizing analysis...")
)

// ── Colors ───────────────────────────────────────────────────────────────────

private val BackgroundDark  = Color(0xFF0A0E1A)
private val OverlayDark     = Color(0x80000000)    // 50 % black overlay
private val CyanAccent      = Color(0xFF00D4FF)
private val BlueAccent      = Color(0xFF007AFF)
private val GreenCheck      = Color(0xFF34C759)
private val TextWhite       = Color(0xFFFFFFFF)
private val TextWhiteMuted  = Color(0xB3FFFFFF)    // 70 % white

// ── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun ForecastAnalysisScreen(viewModel: RouteViewModel) {
    val state by viewModel.state.collectAsState()

    // ── Elapsed seconds ticker ────────────────────────────────────────────
    var elapsedSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000L)
            val startTime = state.analysisStartTime
            elapsedSeconds = if (startTime != null) {
                ((System.currentTimeMillis() - startTime) / 1_000L).toInt()
            } else {
                elapsedSeconds + 1
            }
            if (elapsedSeconds >= TOTAL_SECONDS && state.dataReady) {
                viewModel.completeAnalysis()
                break
            }
        }
    }

    // ── Derived state ─────────────────────────────────────────────────────
    val progress = (elapsedSeconds.toFloat() / TOTAL_SECONDS).coerceIn(0f, 1f)

    val remainingSeconds = (TOTAL_SECONDS - elapsedSeconds).coerceAtLeast(0)
    val timerText = "%d:%02d".format(remainingSeconds / 60, remainingSeconds % 60)

    // Which steps are visible (triggered) and which is current (last triggered)
    val visibleSteps = ANALYSIS_STEPS.filter { it.triggerAt <= elapsedSeconds }
    val currentStepIndex = visibleSteps.lastIndex

    // Route title
    val routeTitle = run {
        val o = state.origin?.icao
        val d = state.destination?.icao
        if (o != null && d != null) "$o → $d" else "Route Analysis"
    }
    val forecastSubtitle = "${state.forecastDays}-day forecast"

    // ── Airplane animation ────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "airplane_anim")

    val hoverOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hover_y"
    )
    val planeRotation by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "plane_rot"
    )

    // ── Animated progress value ───────────────────────────────────────────
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800, easing = LinearEasing),
        label = "progress"
    )

    // ── Layout ────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Dark overlay (simulates the iOS video overlay)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OverlayDark)
        )

        // Subtle radial glow in the center-top area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x1A007AFF),
                            Color.Transparent
                        ),
                        center = Offset(0.5f, 0.3f),
                        radius = 800f
                    )
                )
        )

        // Cancel button — top left
        IconButton(
            onClick = { viewModel.navigateBack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 52.dp, start = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel",
                tint = TextWhite,
                modifier = Modifier.size(24.dp)
            )
        }

        // Main content column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Title ─────────────────────────────────────────────────────
            Text(
                text = "Analyzing Your Route",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            // ── Subtitle ──────────────────────────────────────────────────
            Text(
                text = "This takes about a minute.\nYou can minimize the app —\nwe'll have your report ready\nwhen you come back.",
                fontSize = 15.sp,
                color = TextWhiteMuted,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(36.dp))

            // ── Animated airplane ─────────────────────────────────────────
            Icon(
                imageVector = Icons.Default.Flight,
                contentDescription = null,
                tint = TextWhite,
                modifier = Modifier
                    .size(64.dp)
                    .offset(y = hoverOffset.dp)
                    .rotate(planeRotation)
            )

            Spacer(Modifier.height(28.dp))

            // ── Route title ───────────────────────────────────────────────
            Text(
                text = routeTitle,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextWhite,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = forecastSubtitle,
                fontSize = 14.sp,
                color = TextWhiteMuted,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // ── Progress bar ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1C2333))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(BlueAccent, CyanAccent)
                            )
                        )
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Timer countdown (right-aligned) ───────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = timerText,
                    fontSize = 13.sp,
                    color = TextWhiteMuted
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Checklist steps ───────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ANALYSIS_STEPS.forEachIndexed { index, step ->
                    val isVisible  = elapsedSeconds >= step.triggerAt
                    val isCurrent  = index == currentStepIndex
                    val isComplete = index < currentStepIndex

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(400)) + slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(400)
                        )
                    ) {
                        AnalysisStepRow(
                            label = step.label,
                            isCurrent = isCurrent,
                            isComplete = isComplete
                        )
                    }
                }
            }
        }
    }
}

// ── Step row ──────────────────────────────────────────────────────────────────

@Composable
private fun AnalysisStepRow(
    label: String,
    isCurrent: Boolean,
    isComplete: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Status icon
        Box(
            modifier = Modifier.size(22.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isComplete -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Done",
                        tint = GreenCheck,
                        modifier = Modifier.size(22.dp)
                    )
                }
                isCurrent -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = CyanAccent,
                        trackColor = Color(0xFF1C2333),
                        strokeWidth = 2.dp,
                        strokeCap = StrokeCap.Round
                    )
                }
                else -> {
                    // Future steps are not shown (AnimatedVisibility handles this)
                }
            }
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = label,
            fontSize = 14.sp,
            color = when {
                isComplete -> TextWhiteMuted
                isCurrent  -> TextWhite
                else       -> TextWhiteMuted
            },
            fontWeight = if (isCurrent) FontWeight.Medium else FontWeight.Normal
        )
    }
}
