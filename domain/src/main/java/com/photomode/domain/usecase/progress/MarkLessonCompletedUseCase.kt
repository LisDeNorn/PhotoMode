package com.photomode.domain.usecase.progress

import com.photomode.domain.repository.ProgressRepository

/**
 * Use Case для отметки урока как пройденного
 */
class MarkLessonCompletedUseCase(
    private val repository: ProgressRepository
) {
    suspend operator fun invoke(lessonId: String) {
        repository.markLessonAsCompleted(lessonId)
    }
}







