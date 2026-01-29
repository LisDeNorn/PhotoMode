package com.photomode.domain.usecase.progress

import com.photomode.domain.model.UserProgress
import com.photomode.domain.repository.ProgressRepository

/**
 * Use Case для получения прогресса пользователя
 */
class GetUserProgressUseCase(
    private val repository: ProgressRepository
) {
    suspend operator fun invoke(): UserProgress {
        return repository.getUserProgress()
    }
}







