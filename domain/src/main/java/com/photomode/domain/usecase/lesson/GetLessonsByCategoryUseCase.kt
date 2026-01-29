package com.photomode.domain.usecase.lesson

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonCategory
import com.photomode.domain.repository.LessonRepository

class GetLessonsByCategoryUseCase(
    private val repository: LessonRepository
) {
    suspend operator fun invoke(category: LessonCategory): List<Lesson> {
        return repository.getLessonsByCategory(category)
    }
}







