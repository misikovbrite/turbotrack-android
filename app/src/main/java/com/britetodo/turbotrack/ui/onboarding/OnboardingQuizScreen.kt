package com.britetodo.turbotrack.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.britetodo.turbotrack.theme.TextMuted
import com.britetodo.turbotrack.theme.TextPrimary
import com.britetodo.turbotrack.theme.TextSecondary
import com.britetodo.turbotrack.theme.TurboBlue
import com.britetodo.turbotrack.theme.TurboCard
import com.britetodo.turbotrack.theme.TurboNavy
import com.britetodo.turbotrack.theme.TurboNavyLight
import kotlinx.coroutines.delay

@Composable
fun OnboardingQuizContent(
    step: Int,
    state: OnboardingState,
    onQ1: (String) -> Unit,
    onQ2: (String) -> Unit,
    onQ3: (String) -> Unit,
    onQ4Toggle: (String) -> Unit,
    onQ5: (String) -> Unit,
    onComplete: () -> Unit
) {
    when (step) {
        6 -> QuizStep(
            question = "How do you feel about flying?",
            options = listOf("😌 Love it, no worries", "🙂 Generally comfortable", "😰 A bit anxious", "😱 Very nervous"),
            selected = state.quizQ1,
            onSelect = onQ1
        )
        7 -> QuizStep(
            question = "How often do you fly?",
            options = listOf("✈️ Weekly (frequent flyer)", "🗓 Monthly", "📅 A few times a year", "🌍 Rarely"),
            selected = state.quizQ2,
            onSelect = onQ2
        )
        8 -> QuizStep(
            question = "How familiar are you with turbulence?",
            options = listOf("🎓 Expert – I know the science", "📚 Some knowledge", "🤔 Not very familiar", "❓ Complete beginner"),
            selected = state.quizQ3,
            onSelect = onQ3
        )
        9 -> MultiSelectQuizStep(
            question = "What would help you most?",
            options = listOf(
                "Real-time alerts", "Route forecasts", "Pilot reports",
                "Altitude details", "3-day outlook", "SIGMET warnings",
                "Plain language tips", "Historical data"
            ),
            selected = state.quizQ4,
            onToggle = onQ4Toggle
        )
        10 -> QuizStep(
            question = "When do you check before a flight?",
            options = listOf("📅 Days before", "🌙 Night before", "☀️ Morning of flight", "🏃 At the airport"),
            selected = state.quizQ5,
            onSelect = onQ5
        )
        11 -> SetupAnimationStep()
        12 -> CompleteStep(onComplete = onComplete)
        else -> CompleteStep(onComplete = onComplete)
    }
}

// ─── Single-select Quiz Step ──────────────────────────────────────────────────

@Composable
private fun QuizStep(
    question: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = question,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp
        )
        Spacer(Modifier.height(32.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            options.forEach { option ->
                val isSelected = selected == option
                QuizOptionCard(
                    text = option,
                    isSelected = isSelected,
                    onClick = { onSelect(option) }
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun QuizOptionCard(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) TurboBlue.copy(alpha = 0.15f) else TurboCard
    val borderColor = if (isSelected) TurboBlue else Color.White.copy(alpha = 0.1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = if (isSelected) TextPrimary else TextSecondary,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = TurboBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─── Multi-select Quiz Step ───────────────────────────────────────────────────

@Composable
private fun MultiSelectQuizStep(
    question: String,
    options: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = question,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Select all that apply",
            fontSize = 13.sp,
            color = TextMuted,
            modifier = Modifier.padding(top = 6.dp)
        )
        Spacer(Modifier.height(24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { option ->
                val isSelected = selected.contains(option)
                MultiSelectOptionCard(
                    text = option,
                    isSelected = isSelected,
                    onClick = { onToggle(option) }
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun MultiSelectOptionCard(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) TurboBlue.copy(alpha = 0.15f) else TurboCard
    val borderColor = if (isSelected) TurboBlue else Color.White.copy(alpha = 0.1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        if (isSelected) TurboBlue else Color.Transparent,
                        RoundedCornerShape(6.dp)
                    )
                    .border(
                        1.5.dp,
                        if (isSelected) TurboBlue else Color.White.copy(alpha = 0.3f),
                        RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = text,
                color = if (isSelected) TextPrimary else TextSecondary,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

// ─── Step 11: Setup Animation ─────────────────────────────────────────────────

private val setupSteps = listOf(
    "Loading atmospheric models…",
    "Calibrating turbulence algorithms…",
    "Connecting to weather stations…",
    "Personalizing your experience…"
)

private val testimonials = listOf(
    Triple("★★★★★", "Game changer for nervous flyers!", "Sarah M."),
    Triple("★★★★★", "Finally an app that explains turbulence clearly", "James K."),
    Triple("★★★★★", "Worth every penny for peace of mind", "Maria L."),
    Triple("★★★★★", "Used it on my last 12 flights. Spot on!", "David R."),
    Triple("★★★★★", "Best aviation weather app available", "Emma T.")
)

@Composable
private fun SetupAnimationStep() {
    var currentSetupStep by remember { mutableIntStateOf(0) }
    var showTestimonials by remember { mutableStateOf(false) }
    var testimonialIndex by remember { mutableIntStateOf(0) }
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        repeat(setupSteps.size) { i ->
            delay(500)
            currentSetupStep = i
            progress = (i + 1).toFloat() / setupSteps.size
        }
        delay(800)
        showTestimonials = true
        while (true) {
            delay(3000)
            testimonialIndex = (testimonialIndex + 1) % testimonials.size
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "spinner")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing)),
        label = "spinnerRotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(100.dp),
                color = TurboBlue,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round
            )
            Icon(
                imageVector = Icons.Default.FlightTakeoff,
                contentDescription = null,
                tint = TurboBlue,
                modifier = Modifier
                    .size(40.dp)
                    .rotate(rotation * 0.1f)
            )
        }

        Spacer(Modifier.height(32.dp))

        AnimatedContent(
            targetState = currentSetupStep,
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(200))
            },
            label = "setupStep"
        ) { step ->
            Text(
                text = setupSteps.getOrElse(step) { "" },
                fontSize = 16.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = TurboBlue,
            trackColor = TurboCard
        )

        if (showTestimonials) {
            Spacer(Modifier.height(48.dp))
            AnimatedContent(
                targetState = testimonialIndex,
                transitionSpec = {
                    fadeIn(tween(400)) togetherWith fadeOut(tween(300))
                },
                label = "testimonial"
            ) { index ->
                val (stars, text, author) = testimonials[index]
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TurboCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(stars, color = Color(0xFFFFC107), fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("\"$text\"", color = TextPrimary, fontSize = 14.sp, lineHeight = 20.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("— $author", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ─── Step 12: Complete ────────────────────────────────────────────────────────

@Composable
private fun CompleteStep(onComplete: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "completeScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(600))) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✈️", fontSize = 64.sp)
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "You're Ready to Fly",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Your personalized turbulence forecast is ready",
                    fontSize = 16.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                Spacer(Modifier.height(48.dp))
                Button(
                    onClick = onComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
