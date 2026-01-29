package com.photomode.domain.usecase.lesson

import com.photomode.domain.model.Lesson
import com.photomode.domain.repository.LessonRepository

class GetLessonOfTheDayUseCase(
    private val repository: LessonRepository
) {
    suspend operator fun invoke(): Lesson? {
        return repository.getLessonOfTheDay()
    }
}







