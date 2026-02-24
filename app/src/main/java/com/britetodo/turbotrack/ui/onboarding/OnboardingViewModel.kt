package com.britetodo.turbotrack.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.britetodo.turbotrack.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingState(
    val quizQ1: String = "",
    val quizQ2: String = "",
    val quizQ3: String = "",
    val quizQ4: Set<String> = emptySet(),
    val quizQ5: String = ""
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefsRepo: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun setQ1(answer: String) { _state.value = _state.value.copy(quizQ1 = answer) }
    fun setQ2(answer: String) { _state.value = _state.value.copy(quizQ2 = answer) }
    fun setQ3(answer: String) { _state.value = _state.value.copy(quizQ3 = answer) }
    fun toggleQ4(answer: String) {
        val current = _state.value.quizQ4.toMutableSet()
        if (current.contains(answer)) current.remove(answer) else current.add(answer)
        _state.value = _state.value.copy(quizQ4 = current)
    }
    fun setQ5(answer: String) { _state.value = _state.value.copy(quizQ5 = answer) }

    fun completeOnboarding() {
        viewModelScope.launch {
            val s = _state.value
            prefsRepo.saveQuizAnswers(
                q1 = s.quizQ1,
                q2 = s.quizQ2,
                q3 = s.quizQ3,
                q4 = s.quizQ4,
                q5 = s.quizQ5
            )
            prefsRepo.setOnboardingCompleted(true)
        }
    }
}
