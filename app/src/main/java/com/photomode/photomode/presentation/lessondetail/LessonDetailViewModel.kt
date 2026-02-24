package com.photomode.photomode.presentation.lessondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photomode.domain.usecase.lesson.GetLessonByIdUseCase
import com.photomode.domain.usecase.progress.GetUserProgressUseCase
import com.photomode.domain.usecase.progress.MarkLessonCompletedUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LessonDetailViewModel(
    private val lessonId: String,
    private val getLessonByIdUseCase: GetLessonByIdUseCase,
    private val getUserProgressUseCase: GetUserProgressUseCase,
    private val markLessonCompletedUseCase: MarkLessonCompletedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LessonDetailUiState())
    val state: StateFlow<LessonDetailUiState> = _state.asStateFlow()

    init {
        loadLesson()
    }

    /**
     * Handles business-related user actions.
     * Navigation events are handled in the Route layer.
     */
    fun onAction(action: LessonDetailAction) {
        when (action) {
            LessonDetailAction.RefreshData -> loadLesson()
            LessonDetailAction.NextStep -> goToNextStep()
            LessonDetailAction.PrevStep -> goToPrevStep()
            LessonDetailAction.CompleteLesson -> completeLesson()
            LessonDetailAction.ExitLesson -> Unit
            LessonDetailAction.StartPractice -> Unit
        }
    }

    private fun loadLesson() {
        viewModelScope.launch {
            _state.value = LessonDetailUiState(isLoading = true)

            try {
                val lesson = getLessonByIdUseCase(lessonId)
                    ?: throw IllegalStateException("Lesson not found")

                val userProgress = getUserProgressUseCase()
                val isCompleted = userProgress.completedLessonIds.contains(lessonId)

                if (lesson.steps.isEmpty()) {
                    _state.value = LessonDetailUiState(
                        isLoading = false,
                        lessonTitle = lesson.title,
                        lessonDescription = lesson.shortDescription,
                        error = "No steps in this lesson"
                    )
                } else {
                    _state.value = LessonDetailUiState(
                        isLoading = false,
                        lessonTitle = lesson.title,
                        lessonDescription = lesson.shortDescription,
                        steps = lesson.steps,
                        currentStepIndex = 0,
                        isLessonCompleted = isCompleted,
                        showCompletionDialog = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = LessonDetailUiState(
                    error = e.message ?: "Error loading lesson"
                )
            }
        }
    }

    private fun goToNextStep() {
        _state.update { state ->
            if (state.currentStepIndex < state.steps.lastIndex) {
                state.copy(currentStepIndex = state.currentStepIndex + 1)
            } else {
                state
            }
        }
    }

    private fun goToPrevStep() {
        _state.update { state ->
            if (state.currentStepIndex > 0) {
                state.copy(currentStepIndex = state.currentStepIndex - 1)
            } else {
                state
            }
        }
    }

    private fun completeLesson() {
        viewModelScope.launch {
            try {
                markLessonCompletedUseCase(lessonId)
                _state.update {
                    it.copy(isLessonCompleted = true, showCompletionDialog = true)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLessonCompleted = false,
                        error = e.message ?: "Error completing lesson"
                    )
                }
            }
        }
    }
}
