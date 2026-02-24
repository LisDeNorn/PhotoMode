package com.photomode.domain.usecase.progress

import com.photomode.domain.model.UserProgress
import com.photomode.domain.repository.ProgressRepository

/** Use case for getting user progress. */
class GetUserProgressUseCase(
    private val repository: ProgressRepository
) {
    suspend operator fun invoke(): UserProgress {
        return repository.getUserProgress()
    }
}







