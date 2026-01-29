package com.photomode.photomode.presentation.home

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.Mission
import com.photomode.domain.model.UserProgress
import com.photomode.domain.usecase.home.LessonWithStatus

data class HomeUiState(
    val lessonOfTheDay: Lesson? = null,
    val fundamentalsLessons: List<LessonWithStatus> = emptyList(),  // Уроки с статусами
    val scenariosLessons: List<LessonWithStatus> = emptyList(),     // Уроки с статусами
    val userProgress: UserProgress = UserProgress(),  // Прогресс пользователя
    val currentMission: Mission? = null,  // Текущая миссия
    val isLoading: Boolean = false,
    val error: String? = null,
    val progressPercentage: Int = 0
)