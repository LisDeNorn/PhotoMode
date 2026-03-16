package com.photomode.domain.usecase.home

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonCategory
import com.photomode.domain.model.LessonStatus
import com.photomode.domain.model.Mission
import com.photomode.domain.model.UserProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [SortLessonsByPriorityUseCase].
 * Verifies lesson status assignment, priority sorting, and result limiting.
 */
class SortLessonsByPriorityUseCaseTest {
    private fun lesson(id: String, category: LessonCategory = LessonCategory.FUNDAMENTALS) =
        Lesson(
            id = id,
            title = "Lesson $id",
            category = category,
            shortDescription = "",
            thumbnailImage = "",
            steps = emptyList()
        )

    private fun mission(requiredLessonIds: List<String>) = Mission(
        id = "mission-1",
        title = "Mission 1",
        requiredLessonIds = requiredLessonIds
    )

    private lateinit var useCase: SortLessonsByPriorityUseCase

    @Before
    fun setUp() {
        useCase = SortLessonsByPriorityUseCase()
    }

    @Test
    fun invoke_returnsEmptyList_whenLessonsEmpty() {
        val result = useCase(emptyList(), UserProgress(), null)
        assertTrue(result.isEmpty())
    }

    @Test
    fun invoke_assignsStatusesAndSortsByPriority() {
        val mission = mission(requiredLessonIds = listOf("m1"))
        val lessons = listOf(
            lesson(id = "completed"),
            lesson(id = "m1"),
            lesson(id = "not_started")
        )
        val userProgress = UserProgress(
            completedLessonIds = setOf("completed")
        )

        val result = useCase(
            lessons = lessons,
            userProgress = userProgress,
            currentMission = mission
        )

        assertEquals(
            listOf(
                LessonStatus.REQUIRED_FOR_MISSION,
                LessonStatus.NOT_STARTED,
                LessonStatus.COMPLETED
            ),
            result.map { it.status }
        )
        assertEquals(
            listOf("m1", "not_started", "completed"),
            result.map { it.lesson.id }
        )
    }

    @Test
    fun invoke_placesAllMissionLessonsFirst() {
        val mission = mission(requiredLessonIds = listOf("m1", "m2"))
        val lessons = listOf(
            lesson(id = "other"),
            lesson(id = "m1"),
            lesson(id = "completed"),
            lesson(id = "m2")
        )
        val userProgress = UserProgress(
            completedLessonIds = setOf("completed")
        )

        val result = useCase(
            lessons = lessons,
            userProgress = userProgress,
            currentMission = mission
        )

        val firstTwoIds = result.take(2).map { it.lesson.id }.toSet()
        assertEquals(setOf("m1", "m2"), firstTwoIds)
        assertEquals(LessonStatus.COMPLETED, result.last().status)
        assertEquals("completed", result.last().lesson.id)
    }

    @Test
    fun invoke_marksLessonAsCompleted_whenLessonIsCompletedAndRequiredForMission() {
        val mission = mission(requiredLessonIds = listOf("a"))
        val lessons = listOf(lesson(id = "a"))
        val userProgress = UserProgress(completedLessonIds = setOf("a"))

        val result = useCase(
            lessons = lessons,
            userProgress = userProgress,
            currentMission = mission
        )

        assertEquals(1, result.size)
        assertEquals(LessonStatus.COMPLETED, result[0].status)
    }

    @Test
    fun invoke_marksLessonsAsNotStartedOrCompleted_whenMissionIsNull() {
        val lessons = listOf(
            lesson(id = "completed"),
            lesson(id = "not_started")
        )
        val userProgress = UserProgress(
            completedLessonIds = setOf("completed")
        )

        val result = useCase(
            lessons = lessons,
            userProgress = userProgress,
            currentMission = null
        )

        assertEquals(
            listOf(LessonStatus.NOT_STARTED, LessonStatus.COMPLETED),
            result.map { it.status }
        )
        assertEquals(
            listOf("not_started", "completed"),
            result.map { it.lesson.id }
        )
    }

    @Test
    fun applyLimit_respectsPriorityOrder() {
        val mission = mission(requiredLessonIds = listOf("m1", "m2"))

        val lessons = listOf(
            lesson(id = "m1"),
            lesson(id = "m2"),
            lesson(id = "n1"),
            lesson(id = "n2"),
            lesson(id = "c1")
        )

        val userProgress = UserProgress(
            completedLessonIds = setOf("c1")
        )

        val sorted = useCase(
            lessons = lessons,
            userProgress = userProgress,
            currentMission = mission
        )

        val limited = useCase.applyLimit(sortedLessons = sorted, limit = 3)

        assertEquals(3, limited.size)
        assertEquals(
            listOf(
                LessonStatus.REQUIRED_FOR_MISSION,
                LessonStatus.REQUIRED_FOR_MISSION,
                LessonStatus.NOT_STARTED
            ),
            limited.map { it.status }
        )
    }

    @Test
    fun applyLimit_returnsAllLessons_whenLimitGreaterThanSize() {
        val mission = mission(requiredLessonIds = listOf("m1"))
        val lessons = listOf(
            lesson(id = "m1"),
            lesson(id = "n1")
        )
        val userProgress = UserProgress()

        val sorted = useCase(
            lessons = lessons,
            userProgress = userProgress,
            currentMission = mission
        )
        val limited = useCase.applyLimit(sortedLessons = sorted, limit = 10)

        assertEquals(sorted.map { it.lesson.id }, limited.map { it.lesson.id })
    }

    @Test
    fun applyLimit_returnsEmptyList_whenLimitIsZero() {
        val sorted = useCase(
            lessons = listOf(
                lesson(id = "m1"),
                lesson(id = "n1")
            ),
            userProgress = UserProgress(),
            currentMission = mission(requiredLessonIds = listOf("m1"))
        )

        val limited = useCase.applyLimit(sortedLessons = sorted, limit = 0)

        assertTrue(limited.isEmpty())
    }
}