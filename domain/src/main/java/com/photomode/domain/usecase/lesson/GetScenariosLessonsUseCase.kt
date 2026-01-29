package com.photomode.domain.usecase.lesson

import com.photomode.domain.model.Lesson
import com.photomode.domain.repository.LessonRepository

class GetScenariosLessonsUseCase(
    private val repository: LessonRepository
) {
    suspend operator fun invoke(): List<Lesson> {
        return repository.getScenariosLessons()
    }
}







