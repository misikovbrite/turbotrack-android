package com.britetodo.turbotrack.ui.forecast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.britetodo.turbotrack.data.model.Airport
import com.britetodo.turbotrack.data.model.TurbulenceSeverity
import com.britetodo.turbotrack.theme.TextMuted
import com.britetodo.turbotrack.theme.TextPrimary
import com.britetodo.turbotrack.theme.TextSecondary
import com.britetodo.turbotrack.theme.TurboBackground
import com.britetodo.turbotrack.theme.TurboBlue
import com.britetodo.turbotrack.theme.TurboCard
import com.britetodo.turbotrack.theme.TurboDivider

// ── Palette constants matching iOS ──────────────────────────────────────────
private val DotGreen  = Color(0xFF34C759)
private val DotRed    = Color(0xFFFF3B30)
private val DotOrange = Color(0xFFFF9500)
private val ChipBlue  = Color(0xFF007AFF)

@Composable
fun RouteInputScreen(viewModel: RouteViewModel) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = TurboBackground,
        contentColor = TextPrimary,
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { focusManager.clearFocus() }
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Header ──────────────────────────────────────────────────────
            HeaderSection()

            // ── Direct / Connecting toggle ──────────────────────────────────
            DirectConnectingToggle(
                isDirect = state.isDirect,
                onSelect = { viewModel.setDirect(it) }
            )

            // ── Route Card ─────────────────────────────────────────────────
            RouteCard(
                state = state,
                onOriginTextChange = { viewModel.setOriginQuery(it) },
                onDestinationTextChange = { viewModel.setDestinationQuery(it) },
                onSelectOrigin = { viewModel.selectOrigin(it) },
                onSelectDestination = { viewModel.selectDestination(it) },
                onSwap = {
                    val currentOrigin = state.origin
                    val currentDest = state.destination
                    val currentOriginQuery = state.originQuery
                    val currentDestQuery = state.destinationQuery
                    if (currentDest != null) viewModel.selectOrigin(currentDest)
                    else viewModel.setOriginQuery(currentDestQuery)
                    if (currentOrigin != null) viewModel.selectDestination(currentOrigin)
                    else viewModel.setDestinationQuery(currentOriginQuery)
                }
            )

            // ── Forecast Period Card ────────────────────────────────────────
            ForecastPeriodCard(
                forecastDays = state.forecastDays,
                onSelect = { viewModel.setForecastDays(it) }
            )

            // ── Check Turbulence CTA ────────────────────────────────────────
            val canCheck = state.origin != null && state.destination != null
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.checkTurbulence()
                },
                enabled = canCheck,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChipBlue,
                    disabledContainerColor = Color(0xFFAEAEB2)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AirplanemodeActive,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Check Turbulence",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            // Error message
            state.error?.let { error ->
                Text(
                    text = error,
                    color = DotRed,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // ── Flight number search ────────────────────────────────────────
            FlightNumberSection(
                flightNumber = state.flightNumber,
                isLoading = state.flightSearchLoading,
                error = state.flightSearchError,
                onFlightNumberChange = { viewModel.setFlightNumber(it) },
                onSearch = {
                    focusManager.clearFocus()
                    viewModel.searchByFlightNumber()
                }
            )

            // ── Recent routes ──────────────────────────────────────────────
            if (state.recentHistory.isNotEmpty()) {
                RecentHistorySection(
                    history = state.recentHistory,
                    onSelect = { entry ->
                        focusManager.clearFocus()
                        viewModel.applyHistoryEntry(entry)
                    }
                )
            }

            // ── "What is turbulence?" text button ──────────────────────────
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = { /* show info sheet */ }) {
                    Text(
                        text = "What is turbulence?",
                        color = ChipBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Header ──────────────────────────────────────────────────────────────────

@Composable
private fun HeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AirplanemodeActive,
            contentDescription = null,
            tint = ChipBlue,
            modifier = Modifier.size(44.dp)
        )
        Text(
            text = "Where are you flying?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Get a personalized turbulence forecast for your route",
            fontSize = 15.sp,
            color = TextSecondary,
            lineHeight = 20.sp
        )
    }
}

// ── Direct / Connecting toggle ───────────────────────────────────────────────

@Composable
private fun DirectConnectingToggle(isDirect: Boolean, onSelect: (Boolean) -> Unit) {
    val shape = RoundedCornerShape(12.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(shape)
            .background(Color(0xFFE5E5EA)),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        listOf(true to "Direct", false to "Connecting").forEach { (value, label) ->
            val selected = isDirect == value
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(3.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) ChipBlue else Color.Transparent)
                    .clickable { onSelect(value) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 15.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) Color.White else TextSecondary
                )
            }
        }
    }
}

// ── Route Card ───────────────────────────────────────────────────────────────

@Composable
private fun RouteCard(
    state: RouteUiState,
    onOriginTextChange: (String) -> Unit,
    onDestinationTextChange: (String) -> Unit,
    onSelectOrigin: (Airport) -> Unit,
    onSelectDestination: (Airport) -> Unit,
    onSwap: () -> Unit
) {
    val cardShape = RoundedCornerShape(20.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = cardShape, clip = false),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = TurboCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // FROM field
            AirportField(
                label = "FROM",
                dot = DotGreen,
                query = state.originQuery,
                selectedAirport = state.origin,
                suggestions = state.originSuggestions,
                onTextChange = onOriginTextChange,
                onSelect = onSelectOrigin,
                imeAction = ImeAction.Next
            )

            // Divider with swap button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(TurboDivider.copy(alpha = 0.4f))
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                // Full-width invisible divider row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E5EA))
                            .clickable { onSwap() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap airports",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(TurboDivider.copy(alpha = 0.4f))
            )

            // TO field
            AirportField(
                label = "TO",
                dot = DotRed,
                query = state.destinationQuery,
                selectedAirport = state.destination,
                suggestions = state.destinationSuggestions,
                onTextChange = onDestinationTextChange,
                onSelect = onSelectDestination,
                imeAction = ImeAction.Done
            )
        }
    }
}

// ── Airport Field (FROM / TO) ─────────────────────────────────────────────

@Composable
private fun AirportField(
    label: String,
    dot: Color,
    query: String,
    selectedAirport: Airport?,
    suggestions: List<Airport>,
    onTextChange: (String) -> Unit,
    onSelect: (Airport) -> Unit,
    imeAction: ImeAction
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colored dot indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dot)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Label row
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted,
                    letterSpacing = 0.5.sp
                )

                Spacer(Modifier.height(2.dp))

                if (selectedAirport != null) {
                    // Selected state: show IATA code bold + separator + name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = selectedAirport.iata,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "  ·  ",
                            fontSize = 17.sp,
                            color = TextMuted
                        )
                        Text(
                            text = selectedAirport.city,
                            fontSize = 17.sp,
                            color = TextSecondary
                        )
                    }
                } else {
                    // Typing state: custom text field
                    BasicTextField(
                        value = query,
                        onValueChange = onTextChange,
                        textStyle = TextStyle(
                            fontSize = 17.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Normal
                        ),
                        cursorBrush = SolidColor(ChipBlue),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = imeAction),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.clearFocus() },
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged { isFocused = it.isFocused },
                        decorationBox = { innerTextField ->
                            Box {
                                if (query.isEmpty()) {
                                    Text(
                                        text = if (label == "FROM") "City or airport code" else "City or airport code",
                                        fontSize = 17.sp,
                                        color = TextMuted
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }

            // Clear / chevron for selected airport
            if (selectedAirport != null) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(TextMuted.copy(alpha = 0.3f))
                        .clickable { onTextChange("") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "×",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Suggestions dropdown (inside the card)
        AnimatedVisibility(
            visible = suggestions.isNotEmpty() && selectedAirport == null && query.isNotBlank(),
            enter = expandVertically(animationSpec = tween(180)) + fadeIn(tween(180)),
            exit = shrinkVertically(animationSpec = tween(180)) + fadeOut(tween(180))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F8FA))
            ) {
                Divider(color = TurboDivider.copy(alpha = 0.4f), thickness = 0.5.dp)
                suggestions.take(5).forEachIndexed { index, airport ->
                    SuggestionRow(airport = airport, onClick = { onSelect(airport) })
                    if (index < suggestions.take(5).lastIndex) {
                        Divider(
                            modifier = Modifier.padding(start = 52.dp),
                            color = TurboDivider.copy(alpha = 0.4f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

// ── Suggestion Row ───────────────────────────────────────────────────────────

@Composable
private fun SuggestionRow(airport: Airport, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // IATA badge
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ChipBlue.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = airport.iata,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ChipBlue
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = airport.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                text = "${airport.city}, ${airport.country}",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        Text(
            text = airport.icao,
            fontSize = 12.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium
        )
    }
}

// ── Forecast Period Card ─────────────────────────────────────────────────────

@Composable
private fun ForecastPeriodCard(forecastDays: Int, onSelect: (Int) -> Unit) {
    val cardShape = RoundedCornerShape(20.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = cardShape, clip = false),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = TurboCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Text(
                text = "Forecast Period",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 0.3.sp
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(3, 7, 14).forEach { days ->
                    val selected = forecastDays == days
                    val chipShape = RoundedCornerShape(12.dp)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(chipShape)
                            .background(
                                if (selected) ChipBlue else Color(0xFFE5E5EA)
                            )
                            .clickable { onSelect(days) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$days days",
                            fontSize = 15.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }
    }
}

// ── Flight Number Section ─────────────────────────────────────────────────────

@Composable
private fun FlightNumberSection(
    flightNumber: String,
    isLoading: Boolean,
    error: String?,
    onFlightNumberChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    val cardShape = RoundedCornerShape(20.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = cardShape, clip = false),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = TurboCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                "Search by Flight Number",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 0.3.sp
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.FlightTakeoff,
                    contentDescription = null,
                    tint = ChipBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = flightNumber,
                    onValueChange = onFlightNumberChange,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(ChipBlue),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        Box {
                            if (flightNumber.isEmpty()) {
                                Text("e.g. AA123", fontSize = 16.sp, color = TextMuted)
                            }
                            inner()
                        }
                    }
                )
                Spacer(Modifier.width(8.dp))
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = ChipBlue
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (flightNumber.isNotBlank()) ChipBlue else Color(0xFFE5E5EA))
                            .clickable(enabled = flightNumber.isNotBlank()) { onSearch() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (flightNumber.isNotBlank()) Color.White else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error, color = DotRed, fontSize = 12.sp)
            }
        }
    }
}

// ── Recent History Section ────────────────────────────────────────────────────

@Composable
private fun RecentHistorySection(
    history: List<com.britetodo.turbotrack.data.preferences.HistoryEntry>,
    onSelect: (com.britetodo.turbotrack.data.preferences.HistoryEntry) -> Unit
) {
    val cardShape = RoundedCornerShape(20.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = cardShape, clip = false),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = TurboCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Recent",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    letterSpacing = 0.3.sp
                )
            }
            history.forEachIndexed { idx, entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(entry) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "${entry.originIata} → ${entry.destIata}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            "${entry.originCity} → ${entry.destCity}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(entry.severity, fontSize = 11.sp, color = TextMuted)
                        Text(entry.dateFormatted, fontSize = 11.sp, color = TextMuted)
                    }
                }
                if (idx < history.lastIndex) {
                    Divider(
                        modifier = Modifier.padding(start = 16.dp),
                        color = TurboDivider.copy(alpha = 0.4f),
                        thickness = 0.5.dp
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

