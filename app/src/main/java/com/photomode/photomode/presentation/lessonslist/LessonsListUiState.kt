package com.photomode.photomode.presentation.lessonslist

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonCategory

data class LessonsListUiState(
    val lessons: List<Lesson> = emptyList(),
    val category: LessonCategory? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)







