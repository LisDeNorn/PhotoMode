package com.photomode.domain.usecase.lesson

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonCategory
import com.photomode.domain.model.UserProgress
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [GetLessonOfTheDayUseCase].
 * Verifies lesson selection, caching for the current day, and stable results within the same day.
 */
class GetLessonOfTheDayUseCaseTest {

    private lateinit var cache: FakeLessonOfTheDayCache
    private lateinit var timeSource: FakeTimeSource
    private lateinit var useCase: GetLessonOfTheDayUseCase

    private val lessonA = lesson(id = "a")
    private val lessonB = lesson(id = "b")
    private val lessonC = lesson(id = "c")

    @Before
    fun setUp() {
        cache = FakeLessonOfTheDayCache()
        timeSource = FakeTimeSource(dayEpoch = 100L)
        useCase = GetLessonOfTheDayUseCase(
            cache = cache,
            timeSource = timeSource
        )
    }

    @Test
    fun invoke_returnsNull_whenLessonsEmpty() = runTest {
        val result = useCase(
            lessons = emptyList(),
            userProgress = UserProgress()
        )

        assertNull(result)
    }

    @Test
    fun invoke_returnsCachedLesson_whenCacheHasEntryForCurrentDay() = runTest {
        val lessons = listOf(lessonA, lessonB, lessonC)
        cache.setCachedLessonForDay(dayEpoch = 100L, lessonId = "b")

        val result = requireNotNull(
            useCase(
                lessons = lessons,
                userProgress = UserProgress(completedLessonIds = setOf("a"))
            )
        )

        assertEquals("b", result.id)
    }

    @Test
    fun invoke_repicksLesson_whenCachedLessonIdIsMissing() = runTest {
        val lessons = listOf(lessonA, lessonB, lessonC)
        cache.setCachedLessonForDay(dayEpoch = 100L, lessonId = "missing")

        val result = requireNotNull(
            useCase(
                lessons = lessons,
                userProgress = UserProgress()
            )
        )

        assertEquals(100L, cache.lastStoredDayEpoch)
        assertEquals(result.id, cache.lastStoredLessonId)
    }

    @Test
    fun invoke_returnsUncompletedLesson_whenCacheIsEmpty() = runTest {
        val lessons = listOf(lessonA, lessonB, lessonC)

        val result = requireNotNull(
            useCase(
                lessons = lessons,
                userProgress = UserProgress(completedLessonIds = setOf("a"))
            )
        )

        assertTrue(result.id in setOf("b", "c"))
    }

    @Test
    fun invoke_cachesReturnedLesson_whenCacheIsEmpty() = runTest {
        val lessons = listOf(lessonA, lessonB, lessonC)

        val result = requireNotNull(
            useCase(
                lessons = lessons,
                userProgress = UserProgress(completedLessonIds = setOf("a"))
            )
        )

        assertEquals(100L, cache.lastStoredDayEpoch)
        assertEquals(result.id, cache.lastStoredLessonId)
    }

    @Test
    fun invoke_returnsLessonFromAllLessons_whenAllLessonsCompleted() = runTest {
        val lessons = listOf(lessonA, lessonB, lessonC)

        val result = requireNotNull(
            useCase(
                lessons = lessons,
                userProgress = UserProgress(completedLessonIds = setOf("a", "b", "c"))
            )
        )

        assertTrue(result.id in setOf("a", "b", "c"))
    }

    @Test
    fun invoke_returnsSameLesson_whenLessonGetsCompletedOnSameDay() = runTest {
        val lessons = listOf(lessonA, lessonB, lessonC)

        val firstResult = requireNotNull(
            useCase(
                lessons = lessons,
                userProgress = UserProgress()
            )
        )

        val secondResult = requireNotNull(
            useCase(
                lessons = lessons,
                userProgress = UserProgress(completedLessonIds = setOf(firstResult.id))
            )
        )

        assertEquals(firstResult.id, secondResult.id)
    }

    @Test
    fun invoke_returnsSameLesson_whenLessonsOrderDiffers() = runTest {
        val useCase1 = GetLessonOfTheDayUseCase(
            cache = FakeLessonOfTheDayCache(),
            timeSource = FakeTimeSource(dayEpoch = 100L)
        )
        val useCase2 = GetLessonOfTheDayUseCase(
            cache = FakeLessonOfTheDayCache(),
            timeSource = FakeTimeSource(dayEpoch = 100L)
        )

        val result1 = requireNotNull(
            useCase1(
                lessons = listOf(lessonA, lessonB, lessonC),
                userProgress = UserProgress()
            )
        )

        val result2 = requireNotNull(
            useCase2(
                lessons = listOf(lessonC, lessonA, lessonB),
                userProgress = UserProgress()
            )
        )

        assertEquals(result1.id, result2.id)
    }

    private fun lesson(id: String) = Lesson(
        id = id,
        title = "Title $id",
        category = LessonCategory.FUNDAMENTALS,
        shortDescription = "",
        thumbnailImage = "",
        steps = emptyList()
    )
}