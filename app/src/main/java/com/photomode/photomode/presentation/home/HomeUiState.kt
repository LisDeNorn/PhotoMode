package com.photomode.photomode.presentation.home

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.Mission
import com.photomode.domain.model.UserProgress
import com.photomode.domain.usecase.home.LessonWithStatus

data class HomeUiState(
    val lessonOfTheDay: Lesson? = null,
    val fundamentalsLessons: List<LessonWithStatus> = emptyList(),
    val scenariosLessons: List<LessonWithStatus> = emptyList(),
    val userProgress: UserProgress = UserProgress(),
    val currentMission: Mission? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val progressPercentage: Int = 0
)