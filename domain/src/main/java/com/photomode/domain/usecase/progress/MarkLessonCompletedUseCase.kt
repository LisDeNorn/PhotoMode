package com.photomode.domain.usecase.progress

import com.photomode.domain.repository.ProgressRepository

/** Use case for marking a lesson as completed. */
class MarkLessonCompletedUseCase(
    private val repository: ProgressRepository
) {
    suspend operator fun invoke(lessonId: String) {
        repository.markLessonAsCompleted(lessonId)
    }
}







