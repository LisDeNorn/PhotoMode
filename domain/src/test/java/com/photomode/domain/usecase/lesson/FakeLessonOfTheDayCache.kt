package com.photomode.domain.usecase.lesson

import com.photomode.domain.repository.LessonOfTheDayCache

/**
 * Fake implementation for tests. Stores one pair (dayEpoch, lessonId).
 * [getCachedLessonIdForDay] returns the stored id only if the day matches.
 * Returns the stored lesson id only when requested for the same day.
 */
class FakeLessonOfTheDayCache : LessonOfTheDayCache {

    private var storedDayEpoch: Long? = null
    private var storedLessonId: String? = null

    override suspend fun getCachedLessonIdForDay(dayEpoch: Long): String? {
        return if (storedDayEpoch == dayEpoch) storedLessonId else null
    }

    override suspend fun setCachedLessonForDay(dayEpoch: Long, lessonId: String) {
        storedDayEpoch = dayEpoch
        storedLessonId = lessonId
    }

    /** Exposed for test assertions. */
    val lastStoredDayEpoch: Long? get() = storedDayEpoch
    val lastStoredLessonId: String? get() = storedLessonId
}
