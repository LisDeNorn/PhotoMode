package com.photomode.domain.model

/**
 * Модель прогресса пользователя
 * 
 * Хранит информацию о пройденных уроках.
 *
 */
data class UserProgress(
    val completedLessonIds: Set<String> = emptySet()
)




