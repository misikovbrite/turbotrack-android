package com.britetodo.turbotrack.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.britetodo.turbotrack.data.model.AirSigmet
import com.britetodo.turbotrack.data.model.PIREPReport
import com.britetodo.turbotrack.data.model.TurbulenceSeverity
import com.britetodo.turbotrack.services.AviationWeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val pireps: List<PIREPReport> = emptyList(),
    val sigmets: List<AirSigmet> = emptyList(),
    val selectedPirep: PIREPReport? = null,
    val selectedAltitude: String = "ALL",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val aviationService: AviationWeatherService
) : ViewModel() {

    private val _state = MutableStateFlow(MapUiState(isLoading = true))
    val state: StateFlow<MapUiState> = _state.asStateFlow()

    val altitudeFilters = listOf("ALL", "FL100", "FL180", "FL240", "FL300", "FL340", "FL390")

    init {
        loadData()
        startAutoRefresh()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val pireps = aviationService.getPireps().body() ?: emptyList()
                val sigmets = aviationService.getSigmets().body() ?: emptyList()
                _state.value = _state.value.copy(
                    pireps = pireps.filter { it.lat != null && it.lon != null },
                    sigmets = sigmets.filter { (it.coords?.size ?: 0) >= 3 },
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to load aviation data. Check your connection."
                )
            }
        }
    }

    fun selectPirep(pirep: PIREPReport?) {
        _state.value = _state.value.copy(selectedPirep = pirep)
    }

    fun setAltitudeFilter(altitude: String) {
        _state.value = _state.value.copy(selectedAltitude = altitude)
    }

    fun filteredPireps(): List<PIREPReport> {
        val sel = _state.value.selectedAltitude
        val pireps = _state.value.pireps
        if (sel == "ALL") return pireps
        val flLevel = sel.removePrefix("FL").toIntOrNull() ?: return pireps
        return pireps.filter { pirep ->
            pirep.flightLevel?.let { fl ->
                fl in (flLevel - 50)..(flLevel + 50)
            } ?: false
        }
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(5 * 60 * 1000L) // 5 minutes
                loadData()
            }
        }
    }
}
