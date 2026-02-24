package com.photomode.domain.usecase.progress

import com.photomode.domain.model.UserProgress

/**
 * Use case for calculating user progress percentage.
 * Business logic lives in the use case layer for testability.
 */
class CalculateProgressPercentageUseCase {

    /**
     * @param userProgress user progress
     * @param totalLessons total number of lessons
     * @return progress percentage from 0 to 100
     */
    operator fun invoke(
        userProgress: UserProgress,
        totalLessons: Int
    ): Int {
        if (totalLessons == 0) return 0
        return (userProgress.completedLessonIds.size * 100 / totalLessons).coerceIn(0, 100)
    }
}




