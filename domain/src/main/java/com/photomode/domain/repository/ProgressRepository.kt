package com.photomode.domain.repository

import com.photomode.domain.model.UserProgress

/**
 * Repository для работы с прогрессом пользователя
 * 
 * Отвечает за сохранение и загрузку информации о пройденных уроках.
 */
interface ProgressRepository {
    /**
     * Получить текущий прогресс пользователя
     */
    suspend fun getUserProgress(): UserProgress
    
    /**
     * Отметить урок как пройденный
     */
    suspend fun markLessonAsCompleted(lessonId: String)
    
    /**
     * Проверить, пройден ли урок
     */
    suspend fun isLessonCompleted(lessonId: String): Boolean
    
    /**
     * Получить список ID пройденных уроков
     */
    suspend fun getCompletedLessonIds(): Set<String>
}







