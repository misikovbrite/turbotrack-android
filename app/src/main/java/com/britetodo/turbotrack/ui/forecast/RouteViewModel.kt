package com.britetodo.turbotrack.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.britetodo.turbotrack.data.model.Airport
import com.britetodo.turbotrack.data.model.TurbulenceForecast
import com.britetodo.turbotrack.services.TurbulenceForecastService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ForecastScreen { Input, Analysis, Story, Result }

data class RouteUiState(
    val origin: Airport? = null,
    val destination: Airport? = null,
    val originQuery: String = "",
    val destinationQuery: String = "",
    val originSuggestions: List<Airport> = emptyList(),
    val destinationSuggestions: List<Airport> = emptyList(),
    val isDirect: Boolean = true,
    val forecastDays: Int = 3,
    val currentScreen: ForecastScreen = ForecastScreen.Input,
    val analysisPhase: Int = 0,
    val analysisProgress: Float = 0f,
    val forecastResult: TurbulenceForecast? = null,
    val error: String? = null
)

private val ANALYSIS_PHASES = listOf(
    "Analyzing atmospheric conditions…",
    "Computing wind shear vectors…",
    "Fetching PIREP data…",
    "Checking SIGMET advisories…",
    "Processing altitude profiles…",
    "Calculating route intersections…",
    "Generating turbulence forecast…"
)

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val forecastService: TurbulenceForecastService
) : ViewModel() {

    private val _state = MutableStateFlow(RouteUiState())
    val state: StateFlow<RouteUiState> = _state.asStateFlow()

    val analysisPhases = ANALYSIS_PHASES

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

    fun selectOrigin(airport: Airport) {
        _state.value = _state.value.copy(
            origin = airport,
            originQuery = airport.displayName,
            originSuggestions = emptyList()
        )
    }

    fun selectDestination(airport: Airport) {
        _state.value = _state.value.copy(
            destination = airport,
            destinationQuery = airport.displayName,
            destinationSuggestions = emptyList()
        )
    }

    fun setDirect(direct: Boolean) { _state.value = _state.value.copy(isDirect = direct) }
    fun setForecastDays(days: Int) { _state.value = _state.value.copy(forecastDays = days) }

    fun checkTurbulence() {
        val origin = _state.value.origin ?: return
        val destination = _state.value.destination ?: return

        _state.value = _state.value.copy(
            currentScreen = ForecastScreen.Analysis,
            analysisPhase = 0,
            analysisProgress = 0f,
            error = null
        )

        viewModelScope.launch {
            // Animate phases while fetching
            val fetchJob = launch {
                try {
                    val result = forecastService.getForecast(origin, destination)
                    _state.value = _state.value.copy(forecastResult = result)
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = "Failed to fetch forecast: ${e.message}")
                }
            }

            // Phase animation (~75s total, but we speed it up and wait for data)
            val phaseDelay = 2000L
            ANALYSIS_PHASES.forEachIndexed { index, _ ->
                delay(phaseDelay)
                _state.value = _state.value.copy(
                    analysisPhase = index,
                    analysisProgress = (index + 1).toFloat() / ANALYSIS_PHASES.size
                )
            }

            fetchJob.join()

            if (_state.value.error == null) {
                _state.value = _state.value.copy(currentScreen = ForecastScreen.Story)
            } else {
                _state.value = _state.value.copy(currentScreen = ForecastScreen.Input)
            }
        }
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
}
