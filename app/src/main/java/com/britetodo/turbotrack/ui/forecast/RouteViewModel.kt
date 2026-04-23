package com.britetodo.turbotrack.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.britetodo.turbotrack.data.model.Airport
import com.britetodo.turbotrack.data.model.DailyForecast
import com.britetodo.turbotrack.data.model.TurbulenceForecast
import com.britetodo.turbotrack.data.model.TurbulenceSeverity
import com.britetodo.turbotrack.data.preferences.ForecastHistoryRepository
import com.britetodo.turbotrack.data.preferences.HistoryEntry
import com.britetodo.turbotrack.services.AnalyticsService
import com.britetodo.turbotrack.services.FlightNumberService
import com.britetodo.turbotrack.services.TurbulenceForecastService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// ---------------------------------------------------------------------------
// Enums
// ---------------------------------------------------------------------------

enum class ForecastScreen { Input, Analysis, Story, Result }

enum class RouteMode { DIRECT, CONNECTING }

// ---------------------------------------------------------------------------
// Auxiliary data classes
// ---------------------------------------------------------------------------

data class ForecastAdvice(
    val title: String,
    val detail: String,
    val color: Long,
    val iconName: String
)

data class FlightLevelInfo(
    val level: Int,
    val severity: TurbulenceSeverity,
    val avgShear: Double,
    val maxJet: Double
)

// ---------------------------------------------------------------------------
// UI State
// ---------------------------------------------------------------------------

data class RouteUiState(
    // Core airports
    val origin: Airport? = null,
    val destination: Airport? = null,
    val viaAirport: Airport? = null,

    // Query strings
    val originQuery: String = "",
    val destinationQuery: String = "",
    val viaQuery: String = "",

    // Suggestion lists
    val originSuggestions: List<Airport> = emptyList(),
    val destinationSuggestions: List<Airport> = emptyList(),
    val viaSuggestions: List<Airport> = emptyList(),

    // Route configuration
    val routeMode: RouteMode = RouteMode.DIRECT,
    val isDirect: Boolean = true,
    val forecastDays: Int = 3,

    // Screen / analysis state
    val currentScreen: ForecastScreen = ForecastScreen.Input,
    val analysisPhase: Int = 0,
    val analysisProgress: Float = 0f,
    val forecastResult: TurbulenceForecast? = null,
    val error: String? = null,

    // Analysis timing
    val analysisStartTime: Long? = null,
    val dataReady: Boolean = false,
    val isAnalyzing: Boolean = false,

    // Flight number search
    val flightNumber: String = "",
    val flightSearchLoading: Boolean = false,
    val flightSearchError: String? = null,

    // Recent history
    val recentHistory: List<HistoryEntry> = emptyList()
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val forecastService: TurbulenceForecastService,
    private val analytics: AnalyticsService,
    private val flightNumberService: FlightNumberService,
    private val historyRepository: ForecastHistoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RouteUiState())
    val state: StateFlow<RouteUiState> = _state.asStateFlow()

    init {
        loadHistory()
    }

    // -----------------------------------------------------------------------
    // Convenience accessors (mirror iOS computed properties on RouteViewModel)
    // -----------------------------------------------------------------------

    /** Alias: departure airport (same as origin). */
    private val RouteUiState.departureAirport: Airport? get() = origin

    /** Alias: arrival airport (same as destination). */
    private val RouteUiState.arrivalAirport: Airport? get() = destination

    // -----------------------------------------------------------------------
    // Computed properties (public, read from current state)
    // -----------------------------------------------------------------------

    /** Whether the route is in connecting (via) mode. */
    val isConnecting: Boolean
        get() = _state.value.routeMode == RouteMode.CONNECTING

    /**
     * Worst-case overall severity across all forecast layers.
     * Falls back to NONE when no forecast is present.
     */
    val forecastSeverity: TurbulenceSeverity
        get() = _state.value.forecastResult?.overallSeverity ?: TurbulenceSeverity.NONE

    /** Human-readable forecast horizon label, e.g. "3-day forecast". */
    val forecastHorizonText: String
        get() = "${_state.value.forecastDays}-day forecast"

    /**
     * Formatted route name: "City A → City B".
     * Falls back gracefully when airport names are unavailable.
     */
    /** iOS-matching alias for routeDisplayName. */
    val routeTitle: String
        get() = routeDisplayName

    val routeDisplayName: String
        get() {
            val dep = _state.value.departureAirport
            val arr = _state.value.arrivalAirport
            val depLabel = dep?.city?.takeIf { it.isNotBlank() }
                ?: dep?.name?.takeIf { it.isNotBlank() }
                ?: "Departure"
            val arrLabel = arr?.city?.takeIf { it.isNotBlank() }
                ?: arr?.name?.takeIf { it.isNotBlank() }
                ?: "Arrival"
            return "$depLabel → $arrLabel"
        }

    /**
     * Contextual advice card for the passenger based on forecast severity.
     */
    val forecastAdvice: ForecastAdvice
        get() = when (forecastSeverity) {
            TurbulenceSeverity.NONE -> ForecastAdvice(
                title = "Smooth Flight Expected",
                detail = "No significant turbulence forecasted. Enjoy your flight! " +
                    "Keep seatbelt loosely fastened as a precaution.",
                color = 0xFF34C759,
                iconName = "check_circle"
            )
            TurbulenceSeverity.LIGHT -> ForecastAdvice(
                title = "Light Turbulence Possible",
                detail = "Minor bumps may occur. Very common and not dangerous. " +
                    "Keep seatbelt fastened when seated.",
                color = 0xFFFFCC00,
                iconName = "cloud"
            )
            TurbulenceSeverity.MODERATE -> ForecastAdvice(
                title = "Moderate Turbulence Expected",
                detail = "Expect noticeable bumps. Walking may be difficult. " +
                    "Keep seatbelt fastened, secure loose items, follow crew instructions.",
                color = 0xFFFF9500,
                iconName = "bolt"
            )
            TurbulenceSeverity.SEVERE,
            TurbulenceSeverity.EXTREME -> ForecastAdvice(
                title = "Significant Turbulence Forecasted",
                detail = "Strong turbulence predicted. Keep seatbelt tightly fastened, " +
                    "secure all loose items, follow crew instructions carefully.",
                color = 0xFFFF3B30,
                iconName = "warning"
            )
        }

    /**
     * 15-segment turbulence profile along the primary route leg.
     *
     * The forecast layers are ordered by flight level. To synthesise a
     * horizontal profile we distribute each layer's severity across segments
     * proportional to its position in the sorted layer list, then take the
     * worst severity per bucket.
     */
    val leg1ProfileSegments: List<TurbulenceSeverity>
        get() {
            val layers = _state.value.forecastResult?.layers
            if (layers.isNullOrEmpty()) return List(15) { TurbulenceSeverity.NONE }

            // Buckets indexed 0..14
            val buckets = Array(15) { mutableListOf<TurbulenceSeverity>() }
            val total = layers.size

            layers.forEachIndexed { index, layer ->
                // Project layer index onto [0, 1) and map to bucket
                val t = index.toDouble() / total.toDouble()
                val bucket = (t * 15).toInt().coerceIn(0, 14)
                buckets[bucket].add(layer.severity)
            }

            // Worst severity per bucket; NONE if bucket is empty
            return buckets.map { severities ->
                severities.maxByOrNull { it.ordinal } ?: TurbulenceSeverity.NONE
            }
        }

    /**
     * Per-flight-level breakdown, sorted highest altitude first.
     */
    val flightLevelBreakdown: List<FlightLevelInfo>
        get() {
            val layers = _state.value.forecastResult?.layers
                ?: return emptyList()

            return layers
                .groupBy { it.flightLevel }
                .map { (level, group) ->
                    FlightLevelInfo(
                        level = level,
                        severity = group.maxByOrNull { it.severity.ordinal }?.severity
                            ?: TurbulenceSeverity.NONE,
                        avgShear = group.map { it.windShear }.average(),
                        maxJet = group.maxOfOrNull { it.windSpeed } ?: 0.0
                    )
                }
                .sortedByDescending { it.level }
        }

    /**
     * Per-day severity summary from the forecast result.
     * Returns the model's DailyForecast list directly (already grouped by day).
     */
    val dailyForecast: List<DailyForecast>
        get() = _state.value.forecastResult?.days ?: emptyList()

    /**
     * Human-readable PIREP summary string.
     *
     * The TurbulenceForecast carries a raw pirepCount from the service.
     * We synthesise a breakdown based on the overall severity since individual
     * PIREP reports are not stored in the forecast model.
     */
    val pirepSummary: String
        get() {
            val forecast = _state.value.forecastResult
                ?: return "No recent pilot reports along this route"
            val count = forecast.pirepCount
            if (count == 0) return "No recent pilot reports along this route"

            // Distribute the count across severity labels using the overall severity
            // as the dominant category (mirrors iOS heuristic).
            return when (forecast.overallSeverity) {
                TurbulenceSeverity.NONE -> "$count report${if (count != 1) "s" else ""}: $count smooth"
                TurbulenceSeverity.LIGHT -> {
                    val light = count
                    "$count report${if (count != 1) "s" else ""}: $light light"
                }
                TurbulenceSeverity.MODERATE -> {
                    val mod = (count * 0.6).toInt().coerceAtLeast(1)
                    val light = count - mod
                    buildString {
                        append("$count report${if (count != 1) "s" else ""}: ")
                        append("$mod moderate")
                        if (light > 0) append(", $light light")
                    }
                }
                TurbulenceSeverity.SEVERE,
                TurbulenceSeverity.EXTREME -> {
                    val sev = (count * 0.4).toInt().coerceAtLeast(1)
                    val mod = (count * 0.4).toInt()
                    val light = (count - sev - mod).coerceAtLeast(0)
                    buildString {
                        append("$count report${if (count != 1) "s" else ""}: ")
                        append("$sev severe")
                        if (mod > 0) append(", $mod moderate")
                        if (light > 0) append(", $light light")
                    }
                }
            }
        }

    // -----------------------------------------------------------------------
    // Input handlers — departure / arrival / via
    // -----------------------------------------------------------------------

    fun setOriginQuery(query: String) {
        _state.value = _state.value.copy(
            originQuery = query,
            originSuggestions = Airport.search(query),
            origin = if (query.isBlank()) null else _state.value.origin
        )
    }

    fun setDestinationQuery(query: String) {
        _state.value = _state.value.copy(
            destinationQuery = query,
            destinationSuggestions = Airport.search(query),
            destination = if (query.isBlank()) null else _state.value.destination
        )
    }

    fun setViaQuery(query: String) {
        _state.value = _state.value.copy(
            viaQuery = query,
            viaSuggestions = Airport.search(query),
            viaAirport = if (query.isBlank()) null else _state.value.viaAirport
        )
    }

    /** Update departure suggestions from the current originQuery. */
    fun updateDepartureSuggestions() {
        _state.value = _state.value.copy(
            originSuggestions = Airport.search(_state.value.originQuery)
        )
    }

    /** Update arrival suggestions from the current destinationQuery. */
    fun updateArrivalSuggestions() {
        _state.value = _state.value.copy(
            destinationSuggestions = Airport.search(_state.value.destinationQuery)
        )
    }

    /** Update via suggestions from the current viaQuery. */
    fun updateViaSuggestions() {
        _state.value = _state.value.copy(
            viaSuggestions = Airport.search(_state.value.viaQuery)
        )
    }

    // -----------------------------------------------------------------------
    // Selection handlers
    // -----------------------------------------------------------------------

    fun selectOrigin(airport: Airport) {
        _state.value = _state.value.copy(
            origin = airport,
            originQuery = airport.displayName,
            originSuggestions = emptyList()
        )
    }

    /** Alias matching iOS naming convention. */
    fun selectDeparture(airport: Airport) = selectOrigin(airport)

    fun selectDestination(airport: Airport) {
        _state.value = _state.value.copy(
            destination = airport,
            destinationQuery = airport.displayName,
            destinationSuggestions = emptyList()
        )
    }

    /** Alias matching iOS naming convention. */
    fun selectArrival(airport: Airport) = selectDestination(airport)

    fun selectVia(airport: Airport) {
        _state.value = _state.value.copy(
            viaAirport = airport,
            viaQuery = airport.displayName,
            viaSuggestions = emptyList()
        )
    }

    // -----------------------------------------------------------------------
    // Route configuration
    // -----------------------------------------------------------------------

    fun setDirect(direct: Boolean) {
        _state.value = _state.value.copy(
            isDirect = direct,
            routeMode = if (direct) RouteMode.DIRECT else RouteMode.CONNECTING
        )
    }

    fun setRouteMode(mode: RouteMode) {
        _state.value = _state.value.copy(
            routeMode = mode,
            isDirect = mode == RouteMode.DIRECT
        )
    }

    fun setForecastDays(days: Int) {
        _state.value = _state.value.copy(forecastDays = days)
    }

    // -----------------------------------------------------------------------
    // Forecast lifecycle
    // -----------------------------------------------------------------------

    fun checkTurbulence() {
        val origin = _state.value.origin ?: return
        val destination = _state.value.destination ?: return

        _state.value = _state.value.copy(
            currentScreen = ForecastScreen.Analysis,
            analysisPhase = 0,
            analysisProgress = 0f,
            error = null,
            analysisStartTime = System.currentTimeMillis(),
            dataReady = false,
            isAnalyzing = true
        )

        viewModelScope.launch {
            try {
                val result = forecastService.getForecast(origin, destination)
                _state.value = _state.value.copy(
                    forecastResult = result,
                    dataReady = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to fetch forecast: ${e.message}",
                    dataReady = true // unblock the UI so it can show error
                )
            }
        }
    }

    /** Called by ForecastAnalysisScreen when elapsed >= 75 s AND dataReady == true. */
    fun completeAnalysis() {
        if (_state.value.error != null) {
            _state.value = _state.value.copy(
                currentScreen = ForecastScreen.Input,
                isAnalyzing = false,
                forecastResult = null,
                error = null
            )
        } else {
            val s = _state.value
            val severityName = s.forecastResult?.overallSeverity?.name ?: "UNKNOWN"
            analytics.logForecastGenerated(
                originIata = s.origin?.iata ?: "",
                destinationIata = s.destination?.iata ?: "",
                severity = severityName
            )
            saveCurrentRouteToHistory(severityName)
            _state.value = s.copy(
                currentScreen = ForecastScreen.Story,
                isAnalyzing = false
            )
        }
    }

    fun logShareForecast() {
        val s = _state.value
        analytics.logShareForecast(
            originIata = s.origin?.iata ?: "",
            destinationIata = s.destination?.iata ?: ""
        )
    }

    /** Called when user taps Cancel on the analysis screen. */
    fun navigateBack() {
        _state.value = _state.value.copy(
            currentScreen = ForecastScreen.Input,
            isAnalyzing = false,
            analysisStartTime = null,
            dataReady = false,
            forecastResult = null,
            error = null
        )
    }

    fun navigateToResult() {
        _state.value = _state.value.copy(currentScreen = ForecastScreen.Result)
    }

    fun navigateToInput() {
        _state.value = _state.value.copy(
            currentScreen = ForecastScreen.Input,
            forecastResult = null,
            error = null
        )
    }

    // -----------------------------------------------------------------------
    // Flight number search
    // -----------------------------------------------------------------------

    fun setFlightNumber(s: String) {
        _state.value = _state.value.copy(flightNumber = s, flightSearchError = null)
    }

    fun searchByFlightNumber() {
        val number = _state.value.flightNumber.trim()
        if (number.isBlank()) return

        _state.value = _state.value.copy(
            flightSearchLoading = true,
            flightSearchError = null
        )

        viewModelScope.launch {
            val result = flightNumberService.lookupFlight(number)
            result.fold(
                onSuccess = { route ->
                    val depAirport = Airport.ALL.firstOrNull {
                        it.icao.equals(route.departureIcao, ignoreCase = true)
                    } ?: Airport.search(route.departureCity).firstOrNull()
                      ?: Airport.search(route.departureIcao).firstOrNull()

                    val arrAirport = Airport.ALL.firstOrNull {
                        it.icao.equals(route.arrivalIcao, ignoreCase = true)
                    } ?: Airport.search(route.arrivalCity).firstOrNull()
                      ?: Airport.search(route.arrivalIcao).firstOrNull()

                    var newState = _state.value.copy(flightSearchLoading = false, flightSearchError = null)
                    if (depAirport != null) {
                        newState = newState.copy(
                            origin = depAirport,
                            originQuery = depAirport.displayName,
                            originSuggestions = emptyList()
                        )
                    }
                    if (arrAirport != null) {
                        newState = newState.copy(
                            destination = arrAirport,
                            destinationQuery = arrAirport.displayName,
                            destinationSuggestions = emptyList()
                        )
                    }
                    if (depAirport == null && arrAirport == null) {
                        newState = newState.copy(flightSearchError = "Could not find airports for flight $number")
                    }
                    _state.value = newState
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        flightSearchLoading = false,
                        flightSearchError = e.message ?: "Flight not found"
                    )
                }
            )
        }
    }

    // -----------------------------------------------------------------------
    // History
    // -----------------------------------------------------------------------

    fun loadHistory() {
        _state.value = _state.value.copy(recentHistory = historyRepository.getHistory())
    }

    fun applyHistoryEntry(entry: HistoryEntry) {
        val depAirport = Airport.ALL.firstOrNull { it.iata == entry.originIata }
        val arrAirport = Airport.ALL.firstOrNull { it.iata == entry.destIata }
        var newState = _state.value
        if (depAirport != null) {
            newState = newState.copy(
                origin = depAirport,
                originQuery = depAirport.displayName,
                originSuggestions = emptyList()
            )
        }
        if (arrAirport != null) {
            newState = newState.copy(
                destination = arrAirport,
                destinationQuery = arrAirport.displayName,
                destinationSuggestions = emptyList()
            )
        }
        _state.value = newState
    }

    private fun saveCurrentRouteToHistory(severity: String) {
        val s = _state.value
        val dep = s.origin ?: return
        val arr = s.destination ?: return
        val dateStr = SimpleDateFormat("MMM d", Locale.US).format(Date())
        val entry = HistoryEntry(
            originIata = dep.iata,
            originCity = dep.city,
            destIata = arr.iata,
            destCity = arr.city,
            severity = severity,
            dateFormatted = dateStr
        )
        historyRepository.saveEntry(entry)
        _state.value = _state.value.copy(recentHistory = historyRepository.getHistory())
    }

    // -----------------------------------------------------------------------
    // Full reset
    // -----------------------------------------------------------------------

    /** Resets all route and forecast state to defaults. */
    fun clearRoute() {
        _state.value = RouteUiState(recentHistory = historyRepository.getHistory())
    }
}
