package com.britetodo.turbotrack.ui.reports

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.britetodo.turbotrack.data.model.PIREPReport
import com.britetodo.turbotrack.data.model.TurbulenceSeverity
import com.britetodo.turbotrack.theme.TextMuted
import com.britetodo.turbotrack.theme.TextPrimary
import com.britetodo.turbotrack.theme.TextSecondary
import com.britetodo.turbotrack.theme.TurboBlue
import com.britetodo.turbotrack.theme.TurboBackground
import com.britetodo.turbotrack.theme.TurboCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var searchActive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TurboBackground)
    ) {
        // Header
        Text(
            text = "Pilot Reports",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        // Search bar
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onSearch = { searchActive = false },
                    expanded = searchActive,
                    onExpandedChange = { searchActive = it },
                    placeholder = { Text("Filter by ICAO or aircraft type", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) }
                )
            },
            expanded = searchActive,
            onExpandedChange = { searchActive = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = SearchBarDefaults.colors(containerColor = TurboCard)
        ) {}

        Spacer(Modifier.height(8.dp))

        // Severity filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                FilterChip(
                    selected = state.severityFilter == null,
                    onClick = { viewModel.setSeverityFilter(null) },
                    label = { Text("ALL", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TurboBlue,
                        selectedLabelColor = Color.White,
                        containerColor = TurboCard,
                        labelColor = TextSecondary
                    )
                )
            }
            items(TurbulenceSeverity.entries.filter { it != TurbulenceSeverity.NONE }) { sev ->
                FilterChip(
                    selected = state.severityFilter == sev,
                    onClick = { viewModel.setSeverityFilter(if (state.severityFilter == sev) null else sev) },
                    label = { Text(sev.shortName, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = sev.color.copy(alpha = 0.8f),
                        selectedLabelColor = Color.White,
                        containerColor = TurboCard,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Stats bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(TurboCard, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TurbulenceSeverity.entries.filter { it != TurbulenceSeverity.NONE }.forEach { sev ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = (state.severityCounts[sev] ?: 0).toString(),
                        color = sev.color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(sev.shortName, color = TextMuted, fontSize = 10.sp)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Main list
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TurboBlue)
            }
        } else {
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { viewModel.loadData(isRefresh = true) },
                modifier = Modifier.fillMaxSize()
            ) {
                if (state.filteredPireps.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (state.error != null) state.error!! else "No reports found",
                            color = TextMuted
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        items(state.filteredPireps, key = { it.id ?: it.rawObservation ?: it.obsTime.toString() }) { pirep ->
                            PIREPRow(
                                pirep = pirep,
                                onClick = { viewModel.selectPirep(pirep) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Bottom Sheet
    state.selectedPirep?.let { pirep ->
        ModalBottomSheet(
            onDismissRequest = { viewModel.selectPirep(null) },
            sheetState = sheetState,
            containerColor = TurboCard,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFE5E5EA)) }
        ) {
            PIREPFullDetail(pirep = pirep)
        }
    }
}

@Composable
private fun PIREPRow(pirep: PIREPReport, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TurboCard, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Severity dot
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(pirep.severity.color, CircleShape)
        )
        Spacer(Modifier.width(12.dp))

        // FL
        Text(
            text = pirep.displayFlightLevel,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.width(56.dp)
        )

        // Aircraft type
        Text(
            text = pirep.displayAircraftType,
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )

        // Intensity
        Text(
            text = pirep.severity.shortName,
            color = pirep.severity.color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(60.dp)
        )

        // Time
        Text(
            text = pirep.timeAgo,
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun PIREPFullDetail(pirep: PIREPReport) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(pirep.displayAircraftType, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 20.sp)
                Text("${pirep.displayFlightLevel} • ${pirep.timeAgo}", color = TextSecondary, fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .background(pirep.severity.color.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(pirep.severity.displayName, color = pirep.severity.color, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(20.dp))

        pirep.icaoId?.let { DetailRow("Station", it) }
        pirep.turbulenceType?.let { DetailRow("Type", it) }
        pirep.turbulenceFreq?.let { DetailRow("Frequency", it) }
        pirep.windSpeed?.let { DetailRow("Wind", "${it} kt") }
        pirep.windDirection?.let { DetailRow("Wind Dir", "${it}°") }
        pirep.temperature?.let { DetailRow("Temperature", "${it}°C") }
        pirep.lat?.let { lat ->
            pirep.lon?.let { lon ->
                DetailRow("Position", "%.3f, %.3f".format(lat, lon))
            }
        }

        pirep.rawObservation?.let { raw ->
            Spacer(Modifier.height(16.dp))
            Text("Raw Report", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TurboCard, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(raw, color = TextSecondary, fontSize = 12.sp, lineHeight = 18.sp)
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextMuted, fontSize = 14.sp)
        Text(value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
    Divider(color = Color(0xFFE5E5EA), thickness = 0.5.dp)
}
