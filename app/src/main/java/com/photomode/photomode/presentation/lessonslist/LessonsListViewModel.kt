package com.photomode.photomode.presentation.lessonslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photomode.domain.model.LessonCategory
import com.photomode.domain.usecase.lesson.GetLessonsByCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LessonsListViewModel(
    private val getLessonsByCategoryUseCase: GetLessonsByCategoryUseCase,
    private val category: LessonCategory
) : ViewModel() {

    private val _state = MutableStateFlow(LessonsListUiState())
    val state: StateFlow<LessonsListUiState> = _state.asStateFlow()

    init {
        loadLessons()
    }

    fun onAction(action: LessonsListAction) {
        when (action) {
            is LessonsListAction.RefreshData -> {
                loadLessons()
            }
            // Навигационные действия (OnLessonClick, OnBackClick)
            // обрабатываются в Route, а не в ViewModel
            LessonsListAction.OnBackClick -> {
                // Игнорируем - навигация обрабатывается в Route
            }
            is LessonsListAction.OnLessonClick -> {
                // Игнорируем - навигация обрабатывается в Route
            }
        }
    }

    private fun loadLessons() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, category = category)

            try {
                val lessons = getLessonsByCategoryUseCase(category)

                _state.value = _state.value.copy(
                    lessons = lessons,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Ошибка загрузки уроков"
                )
            }
        }
    }
}

