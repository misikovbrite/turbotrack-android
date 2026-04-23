package com.britetodo.turbotrack.ui.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ── Color constants (matching OnboardingScreen.kt) ────────────────────────────
private val OnboardDark  = Color(0xFF1A1A26)
private val OnboardSub   = Color(0xFF737378)
private val AccentBlue   = Color(0xFF337FF2)
private val CreamColor   = Color(0xFFFAF5EE)
private val DarkSetupBg  = Color(0xFF0A0F1F)

// ── Data classes used by OnboardingScreen.kt ──────────────────────────────────

data class QuizOption(
    val title: String,
    val subtitle: String
)

data class InterestOption(
    val emoji: String,
    val title: String
)

// ── Quiz: single-select ───────────────────────────────────────────────────────

@Composable
fun QuizSingleSelect(
    title: String,
    options: List<QuizOption>,
    selected: String,
    onSelect: (String) -> Unit,
    onContinue: () -> Unit
) {
    val canContinue = selected.isNotEmpty()

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = OnboardDark,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(Modifier.height(28.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { option ->
                    val isSelected = selected == option.title
                    QuizOptionRow(
                        option = option,
                        isSelected = isSelected,
                        onClick = { onSelect(option.title) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        QuizContinueButton(enabled = canContinue, onClick = onContinue)
    }
}

@Composable
private fun QuizOptionRow(
    option: QuizOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) AccentBlue.copy(alpha = 0.08f) else Color.White
    val borderColor = if (isSelected) AccentBlue else OnboardDark.copy(alpha = 0.12f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 0.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(0.06f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnboardDark
                )
                Text(
                    text = option.subtitle,
                    fontSize = 14.sp,
                    color = OnboardSub,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            // Radio circle
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) AccentBlue else OnboardDark.copy(alpha = 0.25f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(AccentBlue, CircleShape)
                    )
                }
            }
        }
    }
}

// ── Quiz: multi-select ────────────────────────────────────────────────────────

@Composable
fun QuizMultiSelect(
    title: String,
    subtitle: String,
    interests: List<InterestOption>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onContinue: () -> Unit
) {
    val canContinue = selected.isNotEmpty()

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = OnboardDark,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Text(
                text = subtitle,
                fontSize = 15.sp,
                color = OnboardSub,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(Modifier.height(24.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                interests.forEach { interest ->
                    val isSelected = selected.contains(interest.title)
                    InterestCheckboxRow(
                        interest = interest,
                        isSelected = isSelected,
                        onClick = { onToggle(interest.title) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        QuizContinueButton(enabled = canContinue, onClick = onContinue)
    }
}

@Composable
private fun InterestCheckboxRow(
    interest: InterestOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) AccentBlue.copy(alpha = 0.08f) else Color.White
    val borderColor = if (isSelected) AccentBlue else OnboardDark.copy(alpha = 0.12f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 0.dp else 2.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black.copy(0.06f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = interest.emoji, fontSize = 18.sp)
            Spacer(Modifier.width(12.dp))
            Text(
                text = interest.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = OnboardDark,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            // Checkbox square
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(if (isSelected) AccentBlue else Color.Transparent)
                    .border(
                        width = if (isSelected) 0.dp else 1.5.dp,
                        color = if (isSelected) Color.Transparent else OnboardDark.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(5.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// ── Quiz continue button (disabled state) ─────────────────────────────────────

@Composable
private fun QuizContinueButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (enabled) AccentBlue else OnboardDark.copy(alpha = 0.18f)
    val textColor = if (enabled) Color.White else OnboardSub

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 32.dp)
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
            text = "Continue",
            color = textColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Step 8: Dark Setup Screen ─────────────────────────────────────────────────

private val setupStepLabels = listOf(
    "Loading atmospheric models...",
    "Calibrating turbulence algorithms...",
    "Connecting to weather stations...",
    "Personalizing your experience..."
)

data class TestimonialData(
    val quote: String,
    val author: String,
    val role: String
)

private val testimonialItems = listOf(
    TestimonialData(
        "Finally I can check turbulence before I fly. Really helps with my anxiety.",
        "Sarah M.", "Frequent traveler"
    ),
    TestimonialData(
        "I use this before every flight. The forecast has been surprisingly accurate.",
        "David K.", "Business traveler"
    ),
    TestimonialData(
        "The flight level breakdown is very useful for pre-flight planning.",
        "Capt. James R.", "Commercial pilot"
    ),
    TestimonialData(
        "Knowing what to expect makes turbulence so much less scary.",
        "Emma L.", "Nervous flyer"
    )
)

@Composable
fun Step8DarkSetup(onComplete: () -> Unit) {
    var currentSetupStep by remember { mutableIntStateOf(-1) }
    var completedSteps by remember { mutableStateOf(setOf<Int>()) }
    var showTrusted by remember { mutableStateOf(false) }
    var progressTarget by remember { mutableFloatStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progressAnim"
    )

    // Orchestrate the setup steps
    LaunchedEffect(Unit) {
        for (i in 0..3) {
            delay(if (i == 0) 400L else 1200L)
            currentSetupStep = i
            progressTarget = (i + 1) / 4f
            delay(900)
            completedSteps = completedSteps + i
        }
        delay(600)
        showTrusted = true
        delay(2500)
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSetupBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))

            // Title
            Text(
                text = "Preparing Your\nForecast Engine",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = CreamColor,
                textAlign = TextAlign.Center,
                lineHeight = 46.sp
            )

            Spacer(Modifier.height(40.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CreamColor)
                )
            }

            Spacer(Modifier.height(36.dp))

            // Setup steps list
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                setupStepLabels.forEachIndexed { index, label ->
                    val isCompleted = completedSteps.contains(index)
                    val isActive = currentSetupStep == index && !isCompleted
                    val alpha by animateFloatAsState(
                        targetValue = if (currentSetupStep >= index) 1f else 0.3f,
                        animationSpec = tween(400),
                        label = "stepAlpha$index"
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Step indicator
                        Box(
                            modifier = Modifier.size(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                isCompleted -> {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(CreamColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = DarkSetupBg,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                                isActive -> {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .border(2.dp, CreamColor, CircleShape)
                                            .background(CreamColor.copy(alpha = 0.2f), CircleShape)
                                    )
                                }
                                else -> {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .border(2.dp, CreamColor.copy(alpha = 0.3f), CircleShape)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Text(
                            text = label,
                            fontSize = 15.sp,
                            color = CreamColor.copy(alpha = alpha),
                            fontWeight = if (isActive || isCompleted) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(Modifier.height(48.dp))

            // Testimonials section
            if (showTrusted) {
                var trustedVisible by remember { mutableStateOf(false) }
                val trustedAlpha by animateFloatAsState(
                    targetValue = if (trustedVisible) 1f else 0f,
                    animationSpec = tween(600),
                    label = "trustedAlpha"
                )
                LaunchedEffect(Unit) { trustedVisible = true }

                Text(
                    text = "Trusted by",
                    fontSize = 18.sp,
                    color = CreamColor.copy(alpha = 0.7f * trustedAlpha),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Travelers & Pilots",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = CreamColor.copy(alpha = trustedAlpha),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // Testimonials carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(testimonialItems) { testimonial ->
                        TestimonialCard(
                            testimonial = testimonial,
                            alpha = trustedAlpha
                        )
                    }
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun TestimonialCard(
    testimonial: TestimonialData,
    alpha: Float
) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.1f * alpha))
            .padding(20.dp)
    ) {
        Column {
            // 5 stars
            Text(
                text = "★★★★★",
                fontSize = 18.sp,
                color = CreamColor.copy(alpha = alpha),
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "\"${testimonial.quote}\"",
                fontSize = 17.sp,
                color = CreamColor.copy(alpha = alpha),
                lineHeight = 24.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "— ${testimonial.author}, ${testimonial.role}",
                fontSize = 14.sp,
                color = CreamColor.copy(alpha = 0.6f * alpha)
            )
        }
    }
}
