package com.photomode.domain.usecase.progress

import com.photomode.domain.model.UserProgress
import com.photomode.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow

/** Use case for observing user progress reactively (Flow). */

class GetUserProgressFlowUseCase(
    private val repository: ProgressRepository
) {
    operator fun invoke(): Flow<UserProgress> = repository.getUserProgressFlow()
}
