package com.photomode.photomode.presentation.lessondetail

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonCategory
import com.photomode.domain.model.LessonStep
import com.photomode.domain.model.UserProgress
import com.photomode.domain.usecase.lesson.GetLessonByIdUseCase
import com.photomode.domain.usecase.progress.GetUserProgressUseCase
import com.photomode.domain.usecase.progress.MarkLessonCompletedUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LessonDetailViewModelTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var getLessonByIdUseCase: GetLessonByIdUseCase
    private lateinit var getUserProgressUseCase: GetUserProgressUseCase
    private lateinit var markLessonCompletedUseCase: MarkLessonCompletedUseCase

    private val lessonId = "lesson_1"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getLessonByIdUseCase = mockk()
        getUserProgressUseCase = mockk()
        markLessonCompletedUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadLesson_success_populatesStateAndCompletionFlag() = runTest {
        val lesson = sampleLesson(hasSteps = true)
        coEvery { getLessonByIdUseCase.invoke(lessonId) } returns lesson
        coEvery { getUserProgressUseCase.invoke() } returns UserProgress(
            completedLessonIds = setOf(lessonId)
        )

        val viewModel = LessonDetailViewModel(
            lessonId = lessonId,
            getLessonByIdUseCase = getLessonByIdUseCase,
            getUserProgressUseCase = getUserProgressUseCase,
            markLessonCompletedUseCase = markLessonCompletedUseCase
        )

        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(lesson.title, state.lessonTitle)
        assertEquals(lesson.shortDescription, state.lessonDescription)
        assertEquals(lesson.steps, state.steps)
        assertEquals(0, state.currentStepIndex)
        assertTrue(state.isLessonCompleted)
        assertFalse(state.showCompletionDialog)
    }

    @Test
    fun loadLesson_withNoSteps_setsErrorState() = runTest {
        val lesson = sampleLesson(hasSteps = false)
        coEvery { getLessonByIdUseCase.invoke(lessonId) } returns lesson
        coEvery { getUserProgressUseCase.invoke() } returns UserProgress()

        val viewModel = LessonDetailViewModel(
            lessonId = lessonId,
            getLessonByIdUseCase = getLessonByIdUseCase,
            getUserProgressUseCase = getUserProgressUseCase,
            markLessonCompletedUseCase = markLessonCompletedUseCase
        )

        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("No steps in this lesson", state.error)
        assertEquals(lesson.title, state.lessonTitle)
        assertEquals(lesson.shortDescription, state.lessonDescription)
        assertTrue(state.steps.isEmpty())
        assertEquals(0, state.currentStepIndex)
        assertFalse(state.isLessonCompleted)
    }

    @Test
    fun loadLesson_whenLessonIsNull_setsNotFoundError() = runTest {
        coEvery { getLessonByIdUseCase.invoke(lessonId) } returns null

        val viewModel = LessonDetailViewModel(
            lessonId = lessonId,
            getLessonByIdUseCase = getLessonByIdUseCase,
            getUserProgressUseCase = getUserProgressUseCase,
            markLessonCompletedUseCase = markLessonCompletedUseCase
        )

        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("Lesson not found", state.error)
        assertTrue(state.steps.isEmpty())
    }

    @Test
    fun loadLesson_whenUseCaseThrows_setsError() = runTest {
        coEvery { getLessonByIdUseCase.invoke(lessonId) } throws IllegalStateException("not found")

        val viewModel = LessonDetailViewModel(
            lessonId = lessonId,
            getLessonByIdUseCase = getLessonByIdUseCase,
            getUserProgressUseCase = getUserProgressUseCase,
            markLessonCompletedUseCase = markLessonCompletedUseCase
        )

        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("not found", state.error)
        assertTrue(state.steps.isEmpty())
    }

    @Test
    fun completeLesson_success_updatesStateAndCallsUseCase() = runTest {
        val lesson = sampleLesson(hasSteps = true)
        coEvery { getLessonByIdUseCase.invoke(lessonId) } returns lesson
        coEvery { getUserProgressUseCase.invoke() } returns UserProgress()
        coEvery { markLessonCompletedUseCase.invoke(lessonId) } returns Unit

        val viewModel = LessonDetailViewModel(
            lessonId = lessonId,
            getLessonByIdUseCase = getLessonByIdUseCase,
            getUserProgressUseCase = getUserProgressUseCase,
            markLessonCompletedUseCase = markLessonCompletedUseCase
        )

        advanceUntilIdle()

        viewModel.onAction(LessonDetailAction.CompleteLesson)
        advanceUntilIdle()

        coVerify(exactly = 1) { markLessonCompletedUseCase.invoke(lessonId) }

        val state = viewModel.state.value
        assertTrue(state.isLessonCompleted)
        assertTrue(state.showCompletionDialog)
        assertNull(state.error)
    }

    @Test
    fun completeLesson_failure_setsErrorAndDoesNotMarkCompleted() = runTest {
        val lesson = sampleLesson(hasSteps = true)
        coEvery { getLessonByIdUseCase.invoke(lessonId) } returns lesson
        coEvery { getUserProgressUseCase.invoke() } returns UserProgress()
        coEvery { markLessonCompletedUseCase.invoke(lessonId) } throws IllegalStateException("failed")

        val viewModel = LessonDetailViewModel(
            lessonId = lessonId,
            getLessonByIdUseCase = getLessonByIdUseCase,
            getUserProgressUseCase = getUserProgressUseCase,
            markLessonCompletedUseCase = markLessonCompletedUseCase
        )

        advanceUntilIdle()

        viewModel.onAction(LessonDetailAction.CompleteLesson)
        advanceUntilIdle()

        coVerify(exactly = 1) { markLessonCompletedUseCase.invoke(lessonId) }

        val state = viewModel.state.value
        assertFalse(state.isLessonCompleted)
        assertEquals("failed", state.error)
        assertFalse(state.showCompletionDialog)
    }

    @Test
    fun refreshData_reloadsLesson() = runTest {
        val firstLesson = sampleLesson(
            title = "Old title",
            shortDescription = "Old description",
            hasSteps = true
        )
        val secondLesson = sampleLesson(
            title = "New title",
            shortDescription = "New description",
            hasSteps = true
        )

        coEvery { getLessonByIdUseCase.invoke(lessonId) } returnsMany listOf(firstLesson, secondLesson)
        coEvery { getUserProgressUseCase.invoke() } returnsMany listOf(
            UserProgress(),
            UserProgress(completedLessonIds = setOf(lessonId))
        )

        val viewModel = LessonDetailViewModel(
            lessonId = lessonId,
            getLessonByIdUseCase = getLessonByIdUseCase,
            getUserProgressUseCase = getUserProgressUseCase,
            markLessonCompletedUseCase = markLessonCompletedUseCase
        )

        advanceUntilIdle()
        assertEquals("Old title", viewModel.state.value.lessonTitle)
        assertFalse(viewModel.state.value.isLessonCompleted)

        viewModel.onAction(LessonDetailAction.RefreshData)
        advanceUntilIdle()

        coVerify(exactly = 2) { getLessonByIdUseCase.invoke(lessonId) }
        coVerify(exactly = 2) { getUserProgressUseCase.invoke() }

        val state = viewModel.state.value
        assertEquals("New title", state.lessonTitle)
        assertEquals("New description", state.lessonDescription)
        assertTrue(state.isLessonCompleted)
        assertEquals(0, state.currentStepIndex)
    }

    @Test
    fun navigation_nextAndPrevStep_respectBounds() = runTest {
        val lesson = sampleLesson(hasSteps = true)

        coEvery { getLessonByIdUseCase.invoke(lessonId) } returns lesson
        coEvery { getUserProgressUseCase.invoke() } returns UserProgress()

        val viewModel = LessonDetailViewModel(
            lessonId = lessonId,
            getLessonByIdUseCase = getLessonByIdUseCase,
            getUserProgressUseCase = getUserProgressUseCase,
            markLessonCompletedUseCase = markLessonCompletedUseCase
        )

        advanceUntilIdle()

        assertEquals(0, viewModel.state.value.currentStepIndex)

        viewModel.onAction(LessonDetailAction.NextStep)
        assertEquals(1, viewModel.state.value.currentStepIndex)

        viewModel.onAction(LessonDetailAction.NextStep)
        assertEquals(1, viewModel.state.value.currentStepIndex)

        viewModel.onAction(LessonDetailAction.PrevStep)
        assertEquals(0, viewModel.state.value.currentStepIndex)

        viewModel.onAction(LessonDetailAction.PrevStep)
        assertEquals(0, viewModel.state.value.currentStepIndex)
    }
    private fun sampleLesson(
        title: String = "Lesson title",
        shortDescription: String = "Short",
        hasSteps: Boolean
    ): Lesson {
        val steps = if (hasSteps) {
            listOf(
                LessonStep.Theory(
                    title = "Step 1",
                    description = "desc",
                    goodExampleImage = "good1",
                    badExampleImage = "bad1"
                ),
                LessonStep.Theory(
                    title = "Step 2",
                    description = "desc2",
                    goodExampleImage = "good2",
                    badExampleImage = "bad2"
                )
            )
        } else {
            emptyList()
        }

        return Lesson(
            id = lessonId,
            title = title,
            category = LessonCategory.FUNDAMENTALS,
            shortDescription = shortDescription,
            thumbnailImage = "thumb",
            steps = steps
        )
    }
}

