package com.photomode.photomode.presentation.lessondetail

import com.photomode.domain.model.LessonStep

/** Lesson detail screen state. Source of truth: [steps], [currentStepIndex], [isLessonCompleted], [showCompletionDialog]. */
data class LessonDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val lessonTitle: String = "",
    val lessonDescription: String = "",

    val steps: List<LessonStep> = emptyList(),
    val currentStepIndex: Int = 0,

    val isLessonCompleted: Boolean = false,
    val showCompletionDialog: Boolean = false
) {
    val stepsCount: Int get() = steps.size
    val stepNumber: Int get() = if (stepsCount == 0) 0 else currentStepIndex + 1
    val currentStep: LessonStep? get() = steps.getOrNull(currentStepIndex)
    val canGoBack: Boolean get() = currentStepIndex > 0
    val canGoNext: Boolean get() = currentStepIndex < steps.lastIndex

    val progress: Float get() = if (stepsCount == 0) 0f else stepNumber.toFloat() / stepsCount
}
