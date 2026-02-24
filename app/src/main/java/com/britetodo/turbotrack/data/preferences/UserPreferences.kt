package com.britetodo.turbotrack.data.preferences

data class UserPreferences(
    val onboardingCompleted: Boolean = false,
    val quizQ1Answer: String = "",  // How do you feel about flying?
    val quizQ2Answer: String = "",  // How often do you fly?
    val quizQ3Answer: String = "",  // Familiarity with turbulence?
    val quizQ4Answers: Set<String> = emptySet(), // What would help most? (multi)
    val quizQ5Answer: String = "",  // When do you check before flight?
    val unitsFeet: Boolean = true,  // true=feet, false=meters
    val notificationsEnabled: Boolean = false,
    val notificationTiming: Int = 24, // hours before flight
    val dataRefreshEnabled: Boolean = true,
    val dataRefreshInterval: Int = 5  // minutes
)
