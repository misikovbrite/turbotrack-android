package com.britetodo.turbotrack.ui.paywall

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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

private val PaywallBackground = Color(0xFF0A0A0F)
private val PaywallCard = Color(0xFF1A1A2E)
private val PaywallTextPrimary = Color.White
private val PaywallTextMuted = Color(0xFFAEAEB2)

private val features = listOf(
    "Route Turbulence Forecast",
    "Search by Flight Number",
    "Up to 14-Day Forecasts",
    "Live Pilot Reports (PIREPs)",
    "Flight Level Breakdown",
    "Real-time Weather Data"
)

private data class TimelineStep(val label: String, val sub: String)

private val timelineSteps = listOf(
    TimelineStep("Today", "Start free trial"),
    TimelineStep("Full Access", "All features"),
    TimelineStep("Day 2", "Reminder"),
    TimelineStep("Day 3", "Cancel anytime")
)

@Composable
fun PaywallScreen(
    source: String = "unknown",
    onDismiss: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val products by viewModel.products.collectAsState()
    var closeVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.analytics.logPaywallShown(source)
        delay(3000)
        closeVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaywallBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(Modifier.height(56.dp)) }

            // Title
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Fly Calm, Every Time",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaywallTextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Know turbulence before you fly",
                        fontSize = 16.sp,
                        color = PaywallTextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Features list
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PaywallCard)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    features.forEach { feature ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(TurboBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(feature, color = PaywallTextPrimary, fontSize = 15.sp)
                        }
                    }
                }
            }

            // Timeline
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PaywallCard)
                        .padding(16.dp)
                ) {
                    Text(
                        "How your trial works",
                        color = PaywallTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        timelineSteps.forEachIndexed { idx, step ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(72.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (idx == 0) TurboBlue else Color(0xFF2C2C3E)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${idx + 1}",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    step.label,
                                    color = PaywallTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    step.sub,
                                    color = PaywallTextMuted,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Weekly plan card
            item {
                val weeklyPrice = products[BillingService.PRODUCT_WEEKLY]
                    ?.subscriptionOfferDetails?.firstOrNull()
                    ?.pricingPhases?.pricingPhaseList?.lastOrNull()?.formattedPrice

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PaywallCard)
                        .border(2.dp, TurboBlue, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Weekly",
                            color = PaywallTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(TurboBlue.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "3-day free trial",
                                color = TurboBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (weeklyPrice != null) "$weeklyPrice/week" else "$4.99/week",
                        color = PaywallTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "after free trial",
                        color = PaywallTextMuted,
                        fontSize = 13.sp
                    )
                }
            }

            // Subscribe button
            item {
                Button(
                    onClick = {
                        viewModel.analytics.logPurchaseStarted(BillingService.PRODUCT_WEEKLY)
                        activity?.let { viewModel.subscribe(it, BillingService.PRODUCT_WEEKLY) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TurboBlue)
                ) {
                    Text(
                        "Start Free Trial",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            // Restore button
            item {
                TextButton(
                    onClick = { viewModel.restorePurchases() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Restore Purchases", color = PaywallTextMuted, fontSize = 14.sp)
                }
            }

            // Footer
            item {
                Text(
                    "Cancel anytime · Privacy Policy · Terms",
                    color = PaywallTextMuted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(40.dp))
            }
        }

        // Close button — appears after 3 seconds
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
                    viewModel.analytics.logPaywallDismissed(source)
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
