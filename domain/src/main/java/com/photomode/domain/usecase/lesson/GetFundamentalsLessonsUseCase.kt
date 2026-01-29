package com.photomode.domain.usecase.lesson

import com.photomode.domain.model.Lesson
import com.photomode.domain.repository.LessonRepository

class GetFundamentalsLessonsUseCase(
    private val repository: LessonRepository
) {
    suspend operator fun invoke(limit: Int = 4): List<Lesson> {
        return repository.getFundamentalsLessons(limit)
    }
}







