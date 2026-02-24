package com.photomode.domain.usecase.progress

import com.photomode.domain.repository.ProgressRepository

/** Use case that checks if a lesson is completed. Wraps ProgressRepository for use-case layer consistency. */
class IsLessonCompletedUseCase(
    private val progressRepository: ProgressRepository
) {
    suspend operator fun invoke(lessonId: String): Boolean =
        progressRepository.isLessonCompleted(lessonId)
}
