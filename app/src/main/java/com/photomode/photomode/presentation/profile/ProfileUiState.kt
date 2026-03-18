package com.photomode.photomode.presentation.profile

import com.photomode.domain.model.Mission

data class ProfileUiState(
    val completedLessons: Int = 0,
    val totalLessons: Int = 0,
    val currentMission: Mission? = null,
    val completedMissionLessons: Int = 0,
    val totalMissionLessons: Int = 0,
    val nextMissionLessonId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val completionPercent: Int
        get() = if (totalLessons == 0) 0 else (completedLessons * 100) / totalLessons

    val missionCompletionPercent: Int
        get() = if (totalMissionLessons == 0) 0 else (completedMissionLessons * 100) / totalMissionLessons
}
