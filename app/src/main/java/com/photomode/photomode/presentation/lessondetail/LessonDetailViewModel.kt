package com.photomode.photomode.presentation.lessondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photomode.domain.model.Lesson
import com.photomode.domain.usecase.lesson.GetLessonByIdUseCase
import com.photomode.domain.usecase.progress.MarkLessonCompletedUseCase
import com.photomode.domain.usecase.progress.GetUserProgressUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LessonDetailViewModel(
    private val lessonId: String,
    private val getLessonByIdUseCase: GetLessonByIdUseCase,
    private val getUserProgressUseCase: GetUserProgressUseCase,
    private val markLessonCompletedUseCase: MarkLessonCompletedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LessonDetailUiState())
    val state: StateFlow<LessonDetailUiState> = _state.asStateFlow()

    private var lesson: Lesson? = null
    private var currentIndex: Int = 0

    init {
        loadLesson()
    }

    fun onAction(action: LessonDetailAction) {
        when (action) {
            LessonDetailAction.RefreshData -> loadLesson()
            LessonDetailAction.NextStep -> goToNextStep()
            LessonDetailAction.PrevStep -> goToPrevStep()
            LessonDetailAction.CompleteLesson -> completeLesson()
            LessonDetailAction.ExitLesson -> {
                // Навигация обрабатывается в Route
            }

        }
    }

    private fun loadLesson() {
        viewModelScope.launch {
            _state.value = LessonDetailUiState(isLoading = true)

            try {
                val lesson = getLessonByIdUseCase(lessonId)
                    ?: throw IllegalStateException("Урок не найден")

                val userProgress = getUserProgressUseCase()
                val isCompleted = userProgress.completedLessonIds.contains(lessonId)

                this@LessonDetailViewModel.lesson = lesson
                currentIndex = 0

                _state.value = buildUiState(isCompleted = isCompleted)

            } catch (e: Exception) {
                _state.value = LessonDetailUiState(
                    error = e.message ?: "Ошибка загрузки урока"
                )
            }
        }
    }
    private fun goToNextStep() {
        val lesson = lesson ?: return

        if (currentIndex < lesson.steps.lastIndex) {
            currentIndex++
            _state.value = buildUiState()
        }
    }

    private fun goToPrevStep() {
        if (currentIndex > 0) {
            currentIndex--
            _state.value = buildUiState()
        }
    }
    private fun completeLesson() {
        viewModelScope.launch {
            try {
                markLessonCompletedUseCase(lessonId)
                _state.value = buildUiState(isCompleted = true).copy(
                    showCompletionDialog = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Ошибка при завершении урока"
                )
            }
        }
    }

    private fun buildUiState(
        isCompleted: Boolean = _state.value.isLessonCompleted,
        showCompletionDialog: Boolean = _state.value.showCompletionDialog
    ): LessonDetailUiState {
        val lesson = lesson ?: return LessonDetailUiState(isLoading = true)

        val stepsCount = lesson.steps.size
        val stepNumber = currentIndex + 1
        val progress = stepNumber.toFloat() / stepsCount
        val isLastStep = currentIndex == stepsCount - 1

        return LessonDetailUiState(
            lessonTitle = lesson.title,
            lessonDescription = lesson.shortDescription,

            stepNumber = stepNumber,
            stepsCount = stepsCount,
            progress = progress,

            currentStep = lesson.steps[currentIndex],

            canGoBack = currentIndex > 0,
            canGoNext = currentIndex < stepsCount - 1,

            canCompleteLesson = isLastStep && !isCompleted,
            isLessonCompleted = isCompleted,
            showCompletionDialog = showCompletionDialog
        )
    }
}




