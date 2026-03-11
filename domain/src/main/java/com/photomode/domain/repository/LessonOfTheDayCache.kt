package com.photomode.domain.repository

/**
 * Persists the chosen "lesson of the day" per day so it does not change
 * when the user completes that lesson (stable for the whole day).
 */
interface LessonOfTheDayCache {

    /**
     * Returns the cached lesson id for the given day, or null if none or day mismatch.
     */
    suspend fun getCachedLessonIdForDay(dayEpoch: Long): String?

    /**
     * Stores the lesson id for the given day.
     */
    suspend fun setCachedLessonForDay(dayEpoch: Long, lessonId: String)
}
