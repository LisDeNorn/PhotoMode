package com.photomode.domain.usecase.lesson

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.UserProgress
import com.photomode.domain.repository.LessonOfTheDayCache
import com.photomode.domain.util.TimeSource
import kotlin.random.Random

/**
 * Picks one "lesson of the day" from uncompleted lessons, stable for the whole day.
 * Uses [LessonOfTheDayCache] so the choice does not change when the user completes that lesson.
 * Uses seeded random (dayEpoch) so the choice does not depend on list order.
 * When all lessons are completed, picks one for review so the block never disappears.
 */
class GetLessonOfTheDayUseCase(
    private val cache: LessonOfTheDayCache,
    private val timeSource: TimeSource
) {

    /**
     * @param lessons Pool to choose from (e.g. fundamentals + scenarios).
     * @param userProgress Used to filter out completed lessons when picking; cache is used for stability.
     * @return The lesson of the day (never null if [lessons] is non-empty).
     */
    suspend operator fun invoke(
        lessons: List<Lesson>,
        userProgress: UserProgress
    ): Lesson? {
        if (lessons.isEmpty()) return null

        val dayEpoch = timeSource.currentDayEpoch()

        val cachedId = cache.getCachedLessonIdForDay(dayEpoch)
        if (cachedId != null) {
            val cachedLesson = lessons.find { it.id == cachedId }
            if (cachedLesson != null) return cachedLesson
            // Cache points to a lesson that is no longer in the list; fall through and repick.
        }

        val uncompleted = lessons.filter { it.id !in userProgress.completedLessonIds }
        val pool = uncompleted.ifEmpty { lessons }
        val sortedPool = pool.sortedBy { it.id }
        val random = Random(dayEpoch)
        val index = random.nextInt(sortedPool.size)
        val chosen = sortedPool[index]
        cache.setCachedLessonForDay(dayEpoch, chosen.id)
        return chosen
    }
}
