package com.britetodo.turbotrack.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.britetodo.turbotrack.data.model.PIREPReport
import com.britetodo.turbotrack.data.model.TurbulenceSeverity
import com.britetodo.turbotrack.services.AviationWeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportsUiState(
    val allPireps: List<PIREPReport> = emptyList(),
    val selectedPirep: PIREPReport? = null,
    val searchQuery: String = "",
    val severityFilter: TurbulenceSeverity? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
) {
    val filteredPireps: List<PIREPReport>
        get() {
            var list = allPireps
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.uppercase()
                list = list.filter { pirep ->
                    pirep.icaoId?.uppercase()?.contains(q) == true ||
                    pirep.aircraftType?.uppercase()?.contains(q) == true
                }
            }
            severityFilter?.let { filter ->
                list = list.filter { it.severity == filter }
            }
            return list.sortedByDescending { it.obsTime }
        }

    val severityCounts: Map<TurbulenceSeverity, Int>
        get() = TurbulenceSeverity.entries.associateWith { sev ->
            allPireps.count { it.severity == sev }
        }
}

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val aviationService: AviationWeatherService
) : ViewModel() {

    private val _state = MutableStateFlow(ReportsUiState(isLoading = true))
    val state: StateFlow<ReportsUiState> = _state.asStateFlow()

    init { loadData() }

    fun loadData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _state.value = _state.value.copy(isRefreshing = true, error = null)
            } else {
                _state.value = _state.value.copy(isLoading = true, error = null)
            }
            try {
                val pireps = aviationService.getPireps().body() ?: emptyList()
                _state.value = _state.value.copy(
                    allPireps = pireps,
                    isLoading = false,
                    isRefreshing = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = "Failed to load reports. Tap to retry."
                )
            }
        }
    }

    fun setSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun setSeverityFilter(severity: TurbulenceSeverity?) {
        _state.value = _state.value.copy(severityFilter = severity)
    }

    fun selectPirep(pirep: PIREPReport?) {
        _state.value = _state.value.copy(selectedPirep = pirep)
    }
}
