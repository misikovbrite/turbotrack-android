package com.britetodo.turbotrack.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.britetodo.turbotrack.BuildConfig
import com.britetodo.turbotrack.theme.TextMuted
import com.britetodo.turbotrack.theme.TextPrimary
import com.britetodo.turbotrack.theme.TextSecondary
import com.britetodo.turbotrack.theme.TurboBlue
import com.britetodo.turbotrack.theme.TurboCard
import com.britetodo.turbotrack.theme.TurboNavy
import com.britetodo.turbotrack.theme.TurboNavyMid
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val prefs by viewModel.prefs.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TurboNavy)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Settings",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        // ── Notifications ──────────────────────────────────────────────────────
        SettingsSection("Notifications") {
            SwitchRow(
                icon = Icons.Default.Notifications,
                title = "Flight Reminders",
                subtitle = "Get notified before your flight",
                checked = prefs?.notificationsEnabled ?: false,
                onCheckedChange = { scope.launch { viewModel.setNotificationsEnabled(it) } }
            )
            if (prefs?.notificationsEnabled == true) {
                TimingRow(
                    selected = prefs?.notificationTiming ?: 24,
                    options = listOf(12, 24, 48),
                    onSelect = { scope.launch { viewModel.setNotificationTiming(it) } }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Units ──────────────────────────────────────────────────────────────
        SettingsSection("Units") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Straighten, contentDescription = null, tint = TurboBlue)
                Text(
                    "Altitude Units",
                    color = TextPrimary,
                    modifier = Modifier.weight(1f).padding(start = 12.dp)
                )
                listOf(true to "Feet", false to "Meters").forEach { (isFeet, label) ->
                    FilterChip(
                        selected = (prefs?.unitsFeet ?: true) == isFeet,
                        onClick = { scope.launch { viewModel.setUnitsFeet(isFeet) } },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TurboBlue,
                            selectedLabelColor = Color.White,
                            containerColor = TurboNavyMid,
                            labelColor = TextSecondary
                        )
                    )
                    Spacer(Modifier.padding(horizontal = 4.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Data Refresh ───────────────────────────────────────────────────────
        SettingsSection("Data Refresh") {
            SwitchRow(
                icon = Icons.Default.Refresh,
                title = "Auto Refresh",
                subtitle = "Automatically refresh aviation data",
                checked = prefs?.dataRefreshEnabled ?: true,
                onCheckedChange = { scope.launch { viewModel.setDataRefreshEnabled(it) } }
            )
            if (prefs?.dataRefreshEnabled == true) {
                TimingRow(
                    selected = prefs?.dataRefreshInterval ?: 5,
                    options = listOf(2, 5, 10, 15),
                    onSelect = { scope.launch { viewModel.setDataRefreshInterval(it) } },
                    suffix = " min"
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Data & Support ─────────────────────────────────────────────────────
        SettingsSection("About") {
            LinkRow(
                icon = Icons.Default.Language,
                title = "Data Sources",
                subtitle = "FAA AWC, Open-Meteo, NOAA",
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://aviationweather.gov")))
                }
            )
            Divider(color = Color.White.copy(alpha = 0.05f))
            LinkRow(
                icon = Icons.Default.Email,
                title = "Contact Support",
                subtitle = "hello@britetodo.com",
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:hello@britetodo.com")
                        putExtra(Intent.EXTRA_SUBJECT, "TurboTrack Support")
                    }
                    context.startActivity(intent)
                }
            )
            Divider(color = Color.White.copy(alpha = 0.05f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = TurboBlue)
                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text("Version", color = TextPrimary, fontSize = 15.sp)
                    Text(BuildConfig.VERSION_NAME, color = TextMuted, fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "TurboTrack provides turbulence forecasts for informational purposes only. Always consult official aviation weather services and follow all crew instructions during flight.",
            color = TextMuted,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(TurboCard, RoundedCornerShape(12.dp))
    ) {
        Text(
            text = title.uppercase(),
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
        content()
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun SwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TurboBlue)
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(title, color = TextPrimary, fontSize = 15.sp)
            Text(subtitle, color = TextMuted, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = TurboBlue,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = TurboNavyMid
            )
        )
    }
}

@Composable
private fun TimingRow(
    selected: Int,
    options: List<Int>,
    onSelect: (Int) -> Unit,
    suffix: String = "h"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 52.dp, end = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        options.forEach { opt ->
            FilterChip(
                selected = selected == opt,
                onClick = { onSelect(opt) },
                label = { Text("$opt$suffix", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TurboBlue,
                    selectedLabelColor = Color.White,
                    containerColor = TurboNavyMid,
                    labelColor = TextSecondary
                )
            )
        }
    }
}

@Composable
private fun LinkRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TurboBlue)
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(title, color = TextPrimary, fontSize = 15.sp)
            Text(subtitle, color = TextMuted, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
    }
}
