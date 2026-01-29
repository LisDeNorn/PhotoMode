package com.photomode.domain.usecase.progress

import com.photomode.domain.model.UserProgress

/**
 * Use Case для вычисления процента прогресса пользователя
 * 
 * Принцип Clean Architecture: бизнес-логика вынесена из модели в Use Case.
 * Это делает код более тестируемым и соответствует принципам разделения ответственности.
 */
class CalculateProgressPercentageUseCase {
    
    /**
     * Вычисляет процент прогресса от общего количества уроков
     * 
     * @param userProgress прогресс пользователя
     * @param totalLessons общее количество уроков
     * @return процент прогресса от 0 до 100
     */
    operator fun invoke(
        userProgress: UserProgress,
        totalLessons: Int
    ): Int {
        if (totalLessons == 0) return 0
        return (userProgress.completedLessonIds.size * 100 / totalLessons).coerceIn(0, 100)
    }
}




