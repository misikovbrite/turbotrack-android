package com.britetodo.turbotrack.ui.paywall

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.britetodo.turbotrack.services.BillingService
import com.britetodo.turbotrack.theme.TurboBlue
import com.britetodo.turbotrack.ui.settings.SettingsViewModel
import kotlinx.coroutines.delay

private val UpsellBackground = Color(0xFF050510)
private val UpsellCard = Color(0xFF12122A)
private val UpsellTextPrimary = Color.White
private val UpsellTextMuted = Color(0xFFAEAEB2)
private val GoldColor = Color(0xFFFFCC00)

private data class UpsellFeature(val text: String)

private val upsellFeatures = listOf(
    UpsellFeature("10x more accurate turbulence prediction"),
    UpsellFeature("Extended 14-day route forecasts"),
    UpsellFeature("Priority real-time PIREP alerts")
)

@Composable
fun UpsellPaywallScreen(
    onDismiss: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val products by viewModel.products.collectAsState()

    var closeVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.analytics.logUpsellViewed()
        contentVisible = true
        delay(1500)
        closeVisible = true
    }

    // Pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UpsellBackground)
    ) {
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(tween(400)) + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(400)
            ),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // Pulsing glow + icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(TurboBlue.copy(alpha = 0.2f))
                    )
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = GoldColor,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Title
                Text(
                    text = "Your forecast is ready.\nNow get Maximum Accuracy",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = UpsellTextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )

                // Features
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(UpsellCard)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    upsellFeatures.forEach { feature ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(GoldColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = GoldColor,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                feature.text,
                                color = UpsellTextPrimary,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // CTA Button
                val superProPrice = products[BillingService.PRODUCT_SUPER_PRO]
                    ?.subscriptionOfferDetails?.firstOrNull()
                    ?.pricingPhases?.pricingPhaseList?.lastOrNull()?.formattedPrice

                Button(
                    onClick = {
                        viewModel.analytics.logUpsellCtaClicked()
                        viewModel.analytics.logUpsellPurchaseStarted(BillingService.PRODUCT_SUPER_PRO)
                        activity?.let { viewModel.subscribe(it, BillingService.PRODUCT_SUPER_PRO) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TurboBlue)
                ) {
                    Text(
                        "Get 3 Days Free",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                // Price subtitle
                Text(
                    text = if (superProPrice != null) "Then $superProPrice/month · Cancel anytime" else "Then \$19.99/month · Cancel anytime",
                    color = UpsellTextMuted,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                // Maybe Later
                TextButton(
                    onClick = {
                        viewModel.analytics.logUpsellClosed()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Maybe Later", color = UpsellTextMuted, fontSize = 15.sp)
                }

                // Footer
                Text(
                    "Privacy Policy · Terms of Service",
                    color = UpsellTextMuted.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))
            }
        }

        // Close button — appears after 1.5 seconds
        AnimatedVisibility(
            visible = closeVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            IconButton(
                onClick = {
                    viewModel.analytics.logUpsellClosed()
                    onDismiss()
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2C2C3E)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
