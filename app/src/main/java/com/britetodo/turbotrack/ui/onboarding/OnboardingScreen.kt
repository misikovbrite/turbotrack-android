package com.britetodo.turbotrack.ui.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.britetodo.turbotrack.R

// ── Onboarding color palette ──────────────────────────────────────────────────
private val OnboardBg       = Color(0xFFEDF5FF)
private val OnboardBgMid    = Color(0xFFE6F0FC)
private val OnboardBg2      = Color(0xFFE0EBFA)
private val OnboardDark     = Color(0xFF1A1A26)
private val OnboardSub      = Color(0xFF737378)
private val AccentBlue      = Color(0xFF337FF2)
private val CreamColor      = Color(0xFFFAF5EE)
private val DarkSetupBg     = Color(0xFF0A0F1F)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val state by viewModel.state.collectAsState()

    // Determine background based on step
    val isDarkStep = currentStep == 8

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDarkStep) {
            // Dark background for step 8
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkSetupBg)
            )
        } else {
            // Light gradient background for steps 0-7
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(OnboardBg, OnboardBgMid, OnboardBg2)
                        )
                    )
            )
            // Floating orbs (light, very transparent)
            LightFloatingOrbs()
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Progress bar: only on steps 1-7
            if (currentStep in 1..7) {
                OnboardingProgressBar(
                    currentStep = currentStep,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(if (currentStep == 0) 48.dp else 16.dp))
            }

            // Step content
            Box(modifier = Modifier.weight(1f)) {
                when (currentStep) {
                    0 -> Step0Welcome(onContinue = { currentStep = 1 })
                    1 -> Step1KnowBeforeYouFly(onContinue = { currentStep = 2 })
                    2 -> Step2UnderstandEveryBump(onContinue = { currentStep = 3 })
                    3 -> Step3RealPilotReports(onContinue = { currentStep = 4 })
                    4 -> Step4Quiz1(
                        selected = state.quizQ1,
                        onSelect = viewModel::setQ1,
                        onContinue = { currentStep = 5 }
                    )
                    5 -> Step5Quiz2(
                        selected = state.quizQ2,
                        onSelect = viewModel::setQ2,
                        onContinue = { currentStep = 6 }
                    )
                    6 -> Step6Quiz3(
                        selected = state.quizQ3,
                        onSelect = viewModel::setQ3,
                        onContinue = { currentStep = 7 }
                    )
                    7 -> Step7Quiz4(
                        selected = state.quizQ4,
                        onToggle = viewModel::toggleQ4,
                        onContinue = { currentStep = 8 }
                    )
                    8 -> Step8DarkSetup(
                        onComplete = {
                            viewModel.completeOnboarding()
                            onComplete()
                        }
                    )
                }
            }
        }
    }
}

// ── Progress bar (steps 1–7, 7 segments) ─────────────────────────────────────

@Composable
private fun OnboardingProgressBar(
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(7) { index ->
            val fraction by animateFloatAsState(
                targetValue = if (index < currentStep) 1f else 0f,
                animationSpec = tween(300),
                label = "progress$index"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(OnboardDark.copy(alpha = 0.12f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(3.dp)
                        .background(AccentBlue)
                )
            }
        }
    }
}

// ── Floating orbs (light theme) ───────────────────────────────────────────────

@Composable
private fun LightFloatingOrbs() {
    val infiniteTransition = rememberInfiniteTransition(label = "orbs")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbFloat"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Orb 1 — top left
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset((-80).dp, (-80 + offsetY).dp)
                .blur(60.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            AccentBlue.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
        // Orb 2 — top right
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(220.dp, (60 - offsetY * 0.6f).dp)
                .blur(50.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            AccentBlue.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
        // Orb 3 — bottom
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(60.dp, (580 + offsetY * 0.4f).dp)
                .blur(55.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            AccentBlue.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
    }
}

// ── Continue button ───────────────────────────────────────────────────────────

@Composable
private fun ContinueButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    label: String = "Continue"
) {
    val bgColor = if (enabled) AccentBlue else OnboardDark.copy(alpha = 0.18f)
    val textColor = if (enabled) Color.White else OnboardSub

    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp, top = 16.dp)
            .fillMaxWidth()
            .height(64.dp)
            .shadow(
                elevation = if (enabled) 12.dp else 0.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = AccentBlue.copy(alpha = 0.3f),
                spotColor = AccentBlue.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(bgColor)
            .then(
                if (enabled) Modifier.clickable(onClick = onClick) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Step 0: Welcome ───────────────────────────────────────────────────────────

@Composable
private fun Step0Welcome(onContinue: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScale"
    )

    var titleVisible by remember { mutableStateOf(false) }
    var subtitleVisible by remember { mutableStateOf(false) }
    val titleAlpha by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "titleAlpha"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (subtitleVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "subtitleAlpha"
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        titleVisible = true
        kotlinx.coroutines.delay(300)
        subtitleVisible = true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo with glow
        Box(contentAlignment = Alignment.Center) {
            // Glow
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(logoScale)
                    .blur(30.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                AccentBlue.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )
            // Logo image
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScale)
                    .clip(RoundedCornerShape(32.dp)),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(48.dp))

        // Title: "Turbulence" (dark) + "Forecast" (accent), each on own line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            val titleOffsetY by animateFloatAsState(
                targetValue = if (titleVisible) 0f else 20f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "titleOffset"
            )
            Text(
                text = "Turbulence",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = OnboardDark.copy(alpha = titleAlpha),
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = titleOffsetY.dp)
            )
            Text(
                text = "Forecast",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AccentBlue.copy(alpha = titleAlpha),
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = titleOffsetY.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "We know flying can be stressful.\nLet's take the uncertainty away.",
            fontSize = 17.sp,
            color = OnboardSub.copy(alpha = subtitleAlpha),
            textAlign = TextAlign.Center,
            lineHeight = 25.sp,
            modifier = Modifier.padding(horizontal = 40.dp)
        )

        Spacer(Modifier.height(60.dp))

        ContinueButton(onClick = onContinue, label = "Continue")
    }
}

// ── Step 1: Know Before You Fly ───────────────────────────────────────────────

@Composable
private fun Step1KnowBeforeYouFly(onContinue: () -> Unit) {
    var iconVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var subtitleVisible by remember { mutableStateOf(false) }

    val iconScale by animateFloatAsState(
        targetValue = if (iconVisible) 1f else 0.5f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
        label = "iconScale"
    )
    val iconAlpha by animateFloatAsState(
        targetValue = if (iconVisible) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "iconAlpha"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "titleAlpha"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (subtitleVisible) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "subtitleAlpha"
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        iconVisible = true
        kotlinx.coroutines.delay(300)
        titleVisible = true
        kotlinx.coroutines.delay(200)
        subtitleVisible = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(8.dp))

            // Route demo card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(iconScale)
                    .then(
                        if (iconAlpha < 1f) Modifier.padding(0.dp) else Modifier
                    )
                    .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = Color.Black.copy(0.08f))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Left: dots + line
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color(0xFF34C759), CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(28.dp)
                                    .background(OnboardDark.copy(alpha = 0.15f))
                            )
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color(0xFFFF3B30), CircleShape)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        // Right: airport fields
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(OnboardDark.copy(alpha = 0.06f))
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "New York (KJFK)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = OnboardDark
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(OnboardDark.copy(alpha = 0.06f))
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "London (EGLL)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = OnboardDark
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "✓",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34C759)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Smooth Flight Expected",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnboardDark
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "3-day forecast",
                            fontSize = 13.sp,
                            color = OnboardSub
                        )
                    }
                }
            }

            Spacer(Modifier.height(36.dp))

            // Title: "Know Before" (accent) + "You Fly" (dark)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Know Before",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AccentBlue.copy(alpha = titleAlpha),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "You Fly",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnboardDark.copy(alpha = titleAlpha),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "Check any route and see exactly\nwhat turbulence to expect",
                fontSize = 17.sp,
                color = OnboardSub.copy(alpha = subtitleAlpha),
                textAlign = TextAlign.Center,
                lineHeight = 25.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(16.dp))
        }

        ContinueButton(onClick = onContinue)
    }
}

// ── Step 2: Understand Every Bump ─────────────────────────────────────────────

data class SeverityCardData(
    val title: String,
    val description: String,
    val color: Color,
    val emoji: String
)

@Composable
private fun Step2UnderstandEveryBump(onContinue: () -> Unit) {
    val cards = listOf(
        SeverityCardData("Smooth", "Calm skies, relax and enjoy", Color(0xFF34C759), "✓"),
        SeverityCardData("Light", "Minor bumps, very common", Color(0xFFFFCC00), "~"),
        SeverityCardData("Moderate", "Noticeable, keep seatbelt on", Color(0xFFFF9500), "!"),
        SeverityCardData("Severe", "Rare, stay firmly buckled", Color(0xFFFF3B30), "!!")
    )

    var titleVisible by remember { mutableStateOf(false) }
    var subtitleVisible by remember { mutableStateOf(false) }
    val cardVisibles = remember { Array(4) { mutableStateOf(false) } }

    val titleAlpha by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "titleAlpha"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (subtitleVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "subtitleAlpha"
    )

    LaunchedEffect(Unit) {
        // Cards staggered
        for (i in 0..3) {
            kotlinx.coroutines.delay(if (i == 0) 200L else 150L)
            cardVisibles[i].value = true
        }
        kotlinx.coroutines.delay(100)
        titleVisible = true
        kotlinx.coroutines.delay(200)
        subtitleVisible = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(8.dp))

            // 4 severity cards
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                cards.forEachIndexed { index, card ->
                    val visible = cardVisibles[index].value
                    val cardAlpha by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = tween(400, easing = FastOutSlowInEasing),
                        label = "cardAlpha$index"
                    )
                    val cardOffset by animateFloatAsState(
                        targetValue = if (visible) 0f else 30f,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
                        label = "cardOffset$index"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = cardOffset.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(14.dp),
                                ambientColor = Color.Black.copy(0.06f)
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = cardAlpha))
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Icon circle
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(card.color.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = card.emoji,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = card.color
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = card.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnboardDark
                                )
                                Text(
                                    text = card.description,
                                    fontSize = 13.sp,
                                    color = OnboardSub,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Understand",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AccentBlue.copy(alpha = titleAlpha),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Every Bump",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnboardDark.copy(alpha = titleAlpha),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "Clear severity levels so you always\nknow what's normal and what's not",
                fontSize = 17.sp,
                color = OnboardSub.copy(alpha = subtitleAlpha),
                textAlign = TextAlign.Center,
                lineHeight = 25.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(16.dp))
        }

        ContinueButton(onClick = onContinue)
    }
}

// ── Step 3: Real Pilot Reports ────────────────────────────────────────────────

data class PirepDemoData(
    val dotColor: Color,
    val route: String,
    val detail: String,
    val time: String
)

@Composable
private fun Step3RealPilotReports(onContinue: () -> Unit) {
    val pireps = listOf(
        PirepDemoData(Color(0xFFFFCC00), "KJFK → EGLL", "FL350 · Light", "12 min ago"),
        PirepDemoData(Color(0xFFFF9500), "KLAX → KORD", "FL380 · Moderate", "28 min ago"),
        PirepDemoData(Color(0xFF34C759), "EDDF → LEMD", "FL310 · Smooth", "45 min ago")
    )

    var titleVisible by remember { mutableStateOf(false) }
    var subtitleVisible by remember { mutableStateOf(false) }
    var cardsVisible by remember { mutableStateOf(false) }

    val titleAlpha by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "titleAlpha"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (subtitleVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "subtitleAlpha"
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        cardsVisible = true
        kotlinx.coroutines.delay(400)
        titleVisible = true
        kotlinx.coroutines.delay(200)
        subtitleVisible = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(8.dp))

            // 3 PIREP cards
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                pireps.forEachIndexed { index, pirep ->
                    var cardVisible by remember { mutableStateOf(false) }
                    val cardAlpha by animateFloatAsState(
                        targetValue = if (cardVisible) 1f else 0f,
                        animationSpec = tween(400, easing = FastOutSlowInEasing),
                        label = "pirepAlpha$index"
                    )
                    val cardOffset by animateFloatAsState(
                        targetValue = if (cardVisible) 0f else 30f,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
                        label = "pirepOffset$index"
                    )

                    LaunchedEffect(cardsVisible) {
                        if (cardsVisible) {
                            kotlinx.coroutines.delay(index * 150L)
                            cardVisible = true
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = cardOffset.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(14.dp),
                                ambientColor = Color.Black.copy(0.06f)
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = cardAlpha))
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(pirep.dotColor, CircleShape)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pirep.route,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnboardDark
                                )
                                Text(
                                    text = pirep.detail,
                                    fontSize = 13.sp,
                                    color = OnboardSub
                                )
                            }
                            Text(
                                text = pirep.time,
                                fontSize = 12.sp,
                                color = OnboardSub
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Real Pilot",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AccentBlue.copy(alpha = titleAlpha),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Reports",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnboardDark.copy(alpha = titleAlpha),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "Live reports from pilots flying\nright now — the same data airlines use",
                fontSize = 17.sp,
                color = OnboardSub.copy(alpha = subtitleAlpha),
                textAlign = TextAlign.Center,
                lineHeight = 25.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(12.dp))

            // Trust badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AccentBlue.copy(alpha = 0.08f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = "🛡️", fontSize = 14.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Powered by NOAA, FAA & Open-Meteo",
                    fontSize = 13.sp,
                    color = OnboardSub
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        ContinueButton(onClick = onContinue)
    }
}

// ── Steps 4-7: Quiz (delegates to OnboardingQuizScreen.kt) ───────────────────

@Composable
private fun Step4Quiz1(
    selected: String,
    onSelect: (String) -> Unit,
    onContinue: () -> Unit
) {
    QuizSingleSelect(
        title = "How do you feel\nabout flying?",
        options = listOf(
            QuizOption("I love flying", "Enjoy every minute in the air"),
            QuizOption("A bit nervous", "Some anxiety, especially during bumps"),
            QuizOption("Quite anxious", "Turbulence really worries me"),
            QuizOption("Fear of flying", "I get very stressed before and during flights")
        ),
        selected = selected,
        onSelect = onSelect,
        onContinue = onContinue
    )
}

@Composable
private fun Step5Quiz2(
    selected: String,
    onSelect: (String) -> Unit,
    onContinue: () -> Unit
) {
    QuizSingleSelect(
        title = "What worries you\nmost about turbulence?",
        options = listOf(
            QuizOption("Is it safe?", "I worry something could go wrong with the plane"),
            QuizOption("Not knowing when", "The sudden, unexpected bumps scare me most"),
            QuizOption("The physical feeling", "Dropping sensations make me panic"),
            QuizOption("No control", "I can't do anything about it and that's scary")
        ),
        selected = selected,
        onSelect = onSelect,
        onContinue = onContinue
    )
}

@Composable
private fun Step6Quiz3(
    selected: String,
    onSelect: (String) -> Unit,
    onContinue: () -> Unit
) {
    QuizSingleSelect(
        title = "How often\ndo you fly?",
        options = listOf(
            QuizOption("Rarely", "Once a year or less"),
            QuizOption("A few times a year", "Holidays and occasional trips"),
            QuizOption("Monthly", "Regular business or personal travel"),
            QuizOption("Weekly", "I'm always in the air")
        ),
        selected = selected,
        onSelect = onSelect,
        onContinue = onContinue
    )
}

@Composable
private fun Step7Quiz4(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onContinue: () -> Unit
) {
    val interests = listOf(
        InterestOption("✈️", "Route turbulence forecast"),
        InterestOption("📊", "Flight level breakdown"),
        InterestOption("🗺️", "Real-time turbulence map"),
        InterestOption("🔔", "Pre-flight reminders"),
        InterestOption("📅", "Up to 14-day forecasts"),
        InterestOption("🧘", "Calming tips for nervous flyers")
    )

    QuizMultiSelect(
        title = "What would\nhelp you most?",
        subtitle = "Select all that apply",
        interests = interests,
        selected = selected,
        onToggle = onToggle,
        onContinue = onContinue
    )
}
