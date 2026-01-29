package com.photomode.domain.usecase.lesson

import com.photomode.domain.model.Lesson
import com.photomode.domain.repository.LessonRepository

class GetLessonByIdUseCase(
    private val repository: LessonRepository
) {
    suspend operator fun invoke(id: String): Lesson? {
        return repository.getLessonById(id)
    }
}







