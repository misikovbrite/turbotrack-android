package com.britetodo.turbotrack.ui.paywall

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.britetodo.turbotrack.R
import com.britetodo.turbotrack.services.BillingService
import com.britetodo.turbotrack.ui.settings.SettingsViewModel
import kotlinx.coroutines.delay
import java.util.Locale

// ── iOS-matching light theme ──────────────────────────────────────────────────
private val PwBackground  = Color(0xFFF5F5F7)
private val PwPrimary     = Color(0xFF1C1C1E)
private val PwSecondary   = Color(0xFF8A8A8E)
private val PwCard        = Color.White
private val PwAccent      = Color(0xFF3380F2)
private val PwAccentLight = Color(0xFF669DFF)

// ── Feature rows (iOS-matching) ───────────────────────────────────────────────
private data class FeatureRow(val icon: ImageVector, val title: String, val desc: String, val highlighted: Boolean = false)
private val featureRows = listOf(
    FeatureRow(Icons.Default.CheckCircle, "Route Turbulence Forecast", "Check any route from departure to arrival", true),
    FeatureRow(Icons.Default.Star,        "Search by Flight Number",   "Enter UA123 or BA456 — get instant forecast", true),
    FeatureRow(Icons.Default.Notifications,"Up to 14-Day Forecasts",   "Choose 3, 7, or 14-day turbulence predictions"),
    FeatureRow(Icons.Default.Check,       "Live Pilot Reports",        "Real-time PIREPs from pilots around the world"),
    FeatureRow(Icons.Default.Check,       "Flight Level Breakdown",    "Detailed analysis at every altitude from FL100 to FL390"),
    FeatureRow(Icons.Default.Check,       "Interactive Turbulence Map","See hotspots and SIGMETs on a live map"),
    FeatureRow(Icons.Default.Notifications,"Pre-flight Notifications", "Get reminded before your flight"),
    FeatureRow(Icons.Default.Check,       "Real-time Weather Data",    "Powered by NOAA, FAA, and Open-Meteo"),
)

// ── Review cards ──────────────────────────────────────────────────────────────
private data class ReviewCard(val text: String, val author: String)
private val reviews = listOf(
    ReviewCard("Finally I can check turbulence before I fly. Really helps with my anxiety about flying.", "Sarah M."),
    ReviewCard("I use this before every flight. The forecast has been surprisingly accurate!", "David K."),
    ReviewCard("The flight level breakdown is very useful for pre-flight planning. Great app!", "Capt. James R."),
    ReviewCard("So simple — enter your route and get instant results. Love it!", "Emma L."),
)

// ── Feature carousel ──────────────────────────────────────────────────────────
private data class CarouselCard(val emoji: String, val title: String, val subtitle: String)
private val carouselItems = listOf(
    CarouselCard("✈️", "Route Forecast",   "Any departure & arrival"),
    CarouselCard("📊", "Flight Levels",    "FL100 to FL390"),
    CarouselCard("🗺️", "Live Map",         "Real-time turbulence"),
    CarouselCard("👨‍✈️", "Pilot Reports",   "Fresh PIREPs"),
    CarouselCard("🔔", "Alerts",           "Pre-flight reminders"),
    CarouselCard("📅", "14-Day Forecast",  "Plan ahead"),
)

@Composable
fun PaywallScreen(
    source: String = "unknown",
    onDismiss: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context  = LocalContext.current
    val activity = context as? Activity
    val products by viewModel.products.collectAsState()

    var selectedPlan    by remember { mutableStateOf(BillingService.PRODUCT_YEARLY) }
    var closeVisible    by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.analytics.logPaywallShown(source)
        delay(viewModel.closeButtonDelayMs)
        closeVisible = true
    }

    val weeklyPrice = products[BillingService.PRODUCT_WEEKLY]
        ?.subscriptionOfferDetails?.firstOrNull()
        ?.pricingPhases?.pricingPhaseList?.lastOrNull()?.formattedPrice ?: "$4.99"
    val yearlyPrice = products[BillingService.PRODUCT_YEARLY]
        ?.subscriptionOfferDetails?.firstOrNull()
        ?.pricingPhases?.pricingPhaseList?.lastOrNull()?.formattedPrice ?: "$19.99"

    // Yearly price broken down to weekly equivalent (19.99 / 52 ≈ 0.38)
    val yearlyWeeklyPrice = run {
        val numeric = yearlyPrice.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 19.99
        val weekly = numeric / 52.0
        val sym = yearlyPrice.firstOrNull { !it.isDigit() && it != '.' && it != ',' } ?: '$'
        "$sym${String.format(Locale.US, "%.2f", weekly)}"
    }

    Box(modifier = Modifier.fillMaxSize().background(PwBackground)) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Title + close button row ─────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 20.dp)
                ) {
                    Text(
                        text = "Fly Calm, Every Time",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = PwPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    AnimatedVisibility(
                        visible = closeVisible,
                        enter = fadeIn(tween(300)),
                        exit  = fadeOut(),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.analytics.logPaywallDismissed(source)
                                onDismiss()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = PwSecondary.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(12.dp)) }

            // ── Before/after image ───────────────────────────────────────────
            item {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.paywall_before_after),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(6.dp, RoundedCornerShape(16.dp))
                )
            }

            item { Spacer(Modifier.height(24.dp)) }

            // ── "How Your Free Trial Works" ──────────────────────────────────
            item {
                Text(
                    text = "How Your Free Trial Works",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PwPrimary
                )
            }

            item { Spacer(Modifier.height(14.dp)) }

            // ── Vertical timeline ────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    TimelineStep(
                        icon = Icons.Default.CheckCircle,
                        title = "Today",
                        desc = "Instant access to all forecasts",
                        isFirst = true, isLast = false
                    )
                    TimelineStep(
                        icon = Icons.Default.Check,
                        title = "Full Access",
                        desc = "Check any route, any time",
                        isFirst = false, isLast = false
                    )
                    TimelineStep(
                        icon = Icons.Default.Notifications,
                        title = "Day 2",
                        desc = "We'll remind you before trial ends",
                        isFirst = false, isLast = false
                    )
                    TimelineStep(
                        icon = Icons.Default.Star,
                        title = "Day 3",
                        desc = "Trial ends. Cancel anytime — no charge",
                        isFirst = false, isLast = true
                    )
                }
            }

            item { Spacer(Modifier.height(20.dp)) }

            // ── Plan cards ───────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(90.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PlanCard(
                        title = "Weekly",
                        price = weeklyPrice,
                        period = "/week",
                        subtitle = "3-day free trial",
                        isSelected = selectedPlan == BillingService.PRODUCT_WEEKLY,
                        onClick = { selectedPlan = BillingService.PRODUCT_WEEKLY },
                        modifier = Modifier.weight(1f)
                    )
                    PlanCard(
                        title = "Yearly",
                        price = yearlyWeeklyPrice,
                        period = "/week",
                        subtitle = "Billed $yearlyPrice/year",
                        isSelected = selectedPlan == BillingService.PRODUCT_YEARLY,
                        onClick = { selectedPlan = BillingService.PRODUCT_YEARLY },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item { Spacer(Modifier.height(10.dp)) }

            // ── 14-day money-back guarantee ──────────────────────────────────
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF34C759),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "14-Day Money-Back Guarantee",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF34C759)
                    )
                }
            }

            item { Spacer(Modifier.height(8.dp)) }

            // ── Trial info text ──────────────────────────────────────────────
            item {
                Text(
                    text = if (selectedPlan == BillingService.PRODUCT_YEARLY)
                        "Billed $yearlyPrice/year, cancel anytime"
                    else
                        "3-day free trial, then $weeklyPrice/week",
                    fontSize = 14.sp,
                    color = PwSecondary,
                    textAlign = TextAlign.Center
                )
            }

            item { Spacer(Modifier.height(10.dp)) }

            // ── Subscribe button ─────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.horizontalGradient(listOf(PwAccent, PwAccentLight))
                        )
                        .clickable {
                            viewModel.analytics.logPurchaseStarted(selectedPlan)
                            activity?.let { viewModel.subscribe(it, selectedPlan) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedPlan == BillingService.PRODUCT_YEARLY) "Continue" else "Try for free",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            item { Spacer(Modifier.height(10.dp)) }

            // ── Restore button ───────────────────────────────────────────────
            item {
                TextButton(onClick = { viewModel.restorePurchases() }) {
                    Text("Restore Purchases", color = PwSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            item { Spacer(Modifier.height(50.dp)) }

            // ── Features section ─────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Fly With Confidence",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PwPrimary
                    )
                    Spacer(Modifier.height(20.dp))
                    featureRows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (row.highlighted) PwAccent.copy(alpha = 0.15f)
                                        else Color(0xFFF2F2F7)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(row.icon, contentDescription = null, tint = PwAccent, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(row.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = PwPrimary)
                                Text(row.desc, fontSize = 14.sp, color = PwSecondary, lineHeight = 19.sp)
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(50.dp)) }

            // ── Reviews section ──────────────────────────────────────────────
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "What Travelers Say",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PwPrimary
                    )
                    Spacer(Modifier.height(16.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp)
                    ) {
                        items(reviews) { review ->
                            Column(
                                modifier = Modifier
                                    .width(280.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(PwCard)
                                    .shadow(4.dp, RoundedCornerShape(16.dp))
                                    .padding(20.dp)
                            ) {
                                Text(review.text, fontSize = 14.sp, color = PwPrimary, lineHeight = 19.sp)
                                Spacer(Modifier.height(8.dp))
                                Row {
                                    repeat(5) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFCC00), modifier = Modifier.size(12.dp))
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                                Text("— ${review.author}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PwSecondary)
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(40.dp)) }

            // ── Feature carousel ─────────────────────────────────────────────
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp)
                ) {
                    items(carouselItems) { card ->
                        Column(
                            modifier = Modifier
                                .width(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(PwAccent.copy(alpha = 0.08f))
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(card.emoji, fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(card.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PwPrimary, textAlign = TextAlign.Center)
                            Text(card.subtitle, fontSize = 13.sp, color = PwSecondary, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(40.dp)) }

            // ── Terms ────────────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Payment will be charged to your Google Account at the confirmation of purchase. " +
                               "Subscription automatically renews unless it is cancelled at least 24 hours before " +
                               "the end of the current period.",
                        fontSize = 11.sp,
                        color = PwSecondary.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Terms of Use", fontSize = 12.sp, color = PwSecondary)
                        Text("Privacy Policy", fontSize = 12.sp, color = PwSecondary)
                    }
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}

// ── Vertical timeline step (iOS-matching) ─────────────────────────────────────
@Composable
private fun TimelineStep(
    icon: ImageVector,
    title: String,
    desc: String,
    isFirst: Boolean,
    isLast: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Left column: connector + icon circle
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (!isFirst) {
                Box(modifier = Modifier.width(2.dp).height(12.dp).background(PwAccent.copy(alpha = 0.3f)))
            }
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(PwAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = PwAccent, modifier = Modifier.size(13.dp))
            }
            if (!isLast) {
                Box(modifier = Modifier.width(2.dp).height(14.dp).background(PwAccent.copy(alpha = 0.3f)))
            }
        }
        // Right column: title + description
        Column {
            if (!isFirst) Spacer(Modifier.height(12.dp))
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = PwPrimary)
            Text(desc, fontSize = 13.sp, color = PwSecondary, lineHeight = 17.sp)
        }
        Spacer(Modifier.weight(1f))
    }
}

// ── Plan card (iOS-matching) ──────────────────────────────────────────────────
@Composable
private fun PlanCard(
    title: String,
    price: String,
    period: String,
    subtitle: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) PwAccent.copy(alpha = 0.08f) else PwCard)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) PwAccent else Color(0xFFE5E5EA),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PwPrimary)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(price, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PwPrimary)
                Text(period, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = PwPrimary)
            }
            if (subtitle != null) {
                Text(subtitle, fontSize = 10.sp, color = PwSecondary)
            }
        }
        if (isSelected) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = PwAccent,
                modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(18.dp)
            )
        }
    }
}
