package com.britetodo.turbotrack.ui.forecast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.britetodo.turbotrack.data.model.Airport
import com.britetodo.turbotrack.theme.TextMuted
import com.britetodo.turbotrack.theme.TextPrimary
import com.britetodo.turbotrack.theme.TextSecondary
import com.britetodo.turbotrack.theme.TurboBlue
import com.britetodo.turbotrack.theme.TurboCard
import com.britetodo.turbotrack.theme.TurboNavyLight

@Composable
fun RouteInputScreen(viewModel: RouteViewModel) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Where are you flying?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Get a personalized turbulence forecast for your route",
            fontSize = 14.sp,
            color = TextSecondary
        )
        Spacer(Modifier.height(20.dp))

        // Origin input
        OutlinedTextField(
            value = state.originQuery,
            onValueChange = { viewModel.setOriginQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("From (ICAO or city)", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.FlightTakeoff, null, tint = TurboBlue) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TurboBlue,
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = TurboBlue,
                focusedContainerColor = TurboCard,
                unfocusedContainerColor = TurboCard
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Origin suggestions
        AnimatedVisibility(
            visible = state.originSuggestions.isNotEmpty() && state.origin == null,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            AirportDropdown(
                airports = state.originSuggestions,
                onSelect = { viewModel.selectOrigin(it) }
            )
        }

        Spacer(Modifier.height(12.dp))

        // Destination input
        OutlinedTextField(
            value = state.destinationQuery,
            onValueChange = { viewModel.setDestinationQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("To (ICAO or city)", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.FlightLand, null, tint = TurboBlue) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TurboBlue,
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = TurboBlue,
                focusedContainerColor = TurboCard,
                unfocusedContainerColor = TurboCard
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Destination suggestions
        AnimatedVisibility(
            visible = state.destinationSuggestions.isNotEmpty() && state.destination == null,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            AirportDropdown(
                airports = state.destinationSuggestions,
                onSelect = { viewModel.selectDestination(it) }
            )
        }

        Spacer(Modifier.height(16.dp))

        // Direct ↔ Connecting toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(true to "Direct", false to "Connecting").forEach { (isDirect, label) ->
                FilterChip(
                    selected = state.isDirect == isDirect,
                    onClick = { viewModel.setDirect(isDirect) },
                    label = { Text(label) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TurboBlue,
                        selectedLabelColor = Color.White,
                        containerColor = TurboCard,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Period chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(3, 7, 14).forEach { days ->
                FilterChip(
                    selected = state.forecastDays == days,
                    onClick = { viewModel.setForecastDays(days) },
                    label = { Text("$days days") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TurboBlue,
                        selectedLabelColor = Color.White,
                        containerColor = TurboCard,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Check Turbulence button
        val canCheck = state.origin != null && state.destination != null
        Button(
            onClick = { viewModel.checkTurbulence() },
            enabled = canCheck,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TurboBlue,
                disabledContainerColor = TurboCard
            )
        ) {
            Icon(Icons.Default.Flight, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(8.dp))
            Text(
                "Check Turbulence",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        state.error?.let { error ->
            Spacer(Modifier.height(12.dp))
            Text(error, color = Color(0xFFF44336), fontSize = 13.sp)
        }
    }
}

@Composable
private fun AirportDropdown(airports: List<Airport>, onSelect: (Airport) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TurboNavyLight, RoundedCornerShape(0.dp, 0.dp, 12.dp, 12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(0.dp, 0.dp, 12.dp, 12.dp))
    ) {
        airports.take(5).forEachIndexed { index, airport ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(airport) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(airport.icao, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("${airport.city}, ${airport.country}", color = TextMuted, fontSize = 12.sp)
                }
                Text(airport.iata, color = TurboBlue, fontSize = 13.sp)
            }
            if (index < airports.size - 1) {
                Divider(color = Color.White.copy(alpha = 0.05f), thickness = 0.5.dp)
            }
        }
    }
}
