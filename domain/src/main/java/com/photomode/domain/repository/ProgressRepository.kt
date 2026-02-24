package com.photomode.domain.repository

import com.photomode.domain.model.UserProgress

/** Repository for user progress (completed lessons). */
interface ProgressRepository {
    suspend fun getUserProgress(): UserProgress
    suspend fun markLessonAsCompleted(lessonId: String)
    suspend fun isLessonCompleted(lessonId: String): Boolean
    suspend fun getCompletedLessonIds(): Set<String>
}







