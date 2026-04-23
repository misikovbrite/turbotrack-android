package com.britetodo.turbotrack.ui.forecast

import android.app.Activity
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.britetodo.turbotrack.services.BillingService
import com.britetodo.turbotrack.ui.settings.SettingsViewModel
import java.util.Locale

private val LimBackground  = Color(0xFFF5F5F7)
private val LimPrimary     = Color(0xFF1C1C1E)
private val LimSecondary   = Color(0xFF8A8A8E)
private val LimAccent      = Color(0xFF3380F2)
private val LimAccentLight = Color(0xFF669DFF)
private val LimGreen       = Color(0xFF34C759)

@Composable
fun ForecastLimitScreen(
    viewModel: RouteViewModel,
    settingsViewModel: SettingsViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val products by settingsViewModel.products.collectAsState()

    val yearlyPrice = products[BillingService.PRODUCT_YEARLY]
        ?.subscriptionOfferDetails?.firstOrNull()
        ?.pricingPhases?.pricingPhaseList?.lastOrNull()?.formattedPrice ?: "$19.99"

    val yearlyWeeklyPrice = run {
        val numeric = yearlyPrice.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 19.99
        val weekly = numeric / 52.0
        val sym = yearlyPrice.firstOrNull { !it.isDigit() && it != '.' && it != ',' } ?: '$'
        "$sym${String.format(Locale.US, "%.2f", weekly)}"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LimBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Lock icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(LimAccent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                tint = LimAccent,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Daily Limit Reached",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = LimPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "You've used your 2 free forecasts for today.\nTry again tomorrow or subscribe for unlimited access.",
            fontSize = 15.sp,
            color = LimSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 21.sp
        )

        Spacer(Modifier.height(32.dp))

        // Yearly plan card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(LimAccent.copy(alpha = 0.08f))
                .border(2.dp, LimAccent, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Yearly Plan", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = LimPrimary)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(yearlyWeeklyPrice, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = LimPrimary)
                    Text("/week", fontSize = 13.sp, color = LimSecondary, modifier = Modifier.padding(bottom = 2.dp))
                }
                Text("Billed $yearlyPrice/year", fontSize = 12.sp, color = LimSecondary)
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = LimGreen, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("14-Day Money-Back Guarantee", fontSize = 12.sp, color = LimGreen, fontWeight = FontWeight.Medium)
                }
            }
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = LimAccent,
                modifier = Modifier.align(Alignment.TopEnd).size(20.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Subscribe button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.horizontalGradient(listOf(LimAccent, LimAccentLight)))
                .clickable {
                    settingsViewModel.analytics.logPurchaseStarted(BillingService.PRODUCT_YEARLY)
                    activity?.let { settingsViewModel.subscribe(it, BillingService.PRODUCT_YEARLY) }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Continue — $yearlyWeeklyPrice/week",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onDismiss) {
            Text("Try Tomorrow", color = LimSecondary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}
