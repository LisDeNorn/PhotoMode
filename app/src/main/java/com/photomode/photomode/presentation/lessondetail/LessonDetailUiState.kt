package com.photomode.photomode.presentation.lessondetail

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonStep

data class LessonDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Top bar
    val lessonTitle: String = "",

    // Progress
    val stepNumber: Int = 0,   // 1-based
    val stepsCount: Int = 0,
    val progress: Float =0.0F,

    // Content
    val lessonDescription: String = "",
    val currentStep: LessonStep? = null,

    // Navigation
    val canGoBack: Boolean = false,
    val canGoNext: Boolean = false,

    // Completion
    val canCompleteLesson: Boolean = false,
    val isLessonCompleted: Boolean = false,
    val showCompletionDialog: Boolean = false
)




