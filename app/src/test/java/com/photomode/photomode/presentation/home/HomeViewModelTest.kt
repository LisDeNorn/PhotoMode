package com.photomode.photomode.presentation.home

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonCategory
import com.photomode.domain.model.LessonStatus
import com.photomode.domain.model.Mission
import com.photomode.domain.model.UserProgress
import com.photomode.domain.usecase.home.GetHomeDataUseCase
import com.photomode.domain.usecase.home.HomeData
import com.photomode.domain.usecase.home.LessonWithStatus
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun state_hasLoadingTrue_initially() = runTest {
        val getHomeDataUseCase: GetHomeDataUseCase = mockk()
        every { getHomeDataUseCase.invoke() } returns flowOf(sampleHomeData())

        val viewModel = HomeViewModel(getHomeDataUseCase)

        assertTrue(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun state_emitsSuccess_whenFirstSubscribed() = runTest {
        val homeData = sampleHomeData()
        val getHomeDataUseCase: GetHomeDataUseCase = mockk()
        every { getHomeDataUseCase.invoke() } returns flowOf(homeData)

        val viewModel = HomeViewModel(getHomeDataUseCase)

        val job = backgroundScope.launch {
            viewModel.state.collect { }
        }

        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertNull(state.error)
        assertEquals(homeData.lessonOfTheDay, state.lessonOfTheDay)
        assertEquals(homeData.fundamentalsLessons, state.fundamentalsLessons)
        assertEquals(homeData.scenariosLessons, state.scenariosLessons)
        assertEquals(homeData.currentMission, state.currentMission)
        assertEquals(homeData.userProgress, state.userProgress)

        job.cancel()
    }

    @Test
    fun state_emitsError_whenUseCaseFails() = runTest {
        val getHomeDataUseCase: GetHomeDataUseCase = mockk()
        every { getHomeDataUseCase.invoke() } returns flow {
            throw IllegalStateException("boom")
        }

        val viewModel = HomeViewModel(getHomeDataUseCase)

        val job = backgroundScope.launch {
            viewModel.state.collect { }
        }

        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals("boom", state.error)

        job.cancel()
    }

    @Test
    fun onAction_refreshData_reloadsHomeData() = runTest {
        val first = sampleHomeData(lessonId = "first")
        val second = sampleHomeData(lessonId = "second")

        val getHomeDataUseCase: GetHomeDataUseCase = mockk()
        every { getHomeDataUseCase.invoke() } returnsMany listOf(
            flowOf(first),
            flowOf(second)
        )

        val viewModel = HomeViewModel(getHomeDataUseCase)

        val job = backgroundScope.launch {
            viewModel.state.collect { }
        }

        advanceUntilIdle()
        assertEquals("first", viewModel.state.value.lessonOfTheDay?.id)

        viewModel.onAction(HomeAction.RefreshData)
        advanceUntilIdle()

        assertEquals("second", viewModel.state.value.lessonOfTheDay?.id)

        job.cancel()
    }

    @Test
    fun onAction_nonRefreshActions_doNotChangeState() = runTest {
        val homeData = sampleHomeData()
        val getHomeDataUseCase: GetHomeDataUseCase = mockk()
        every { getHomeDataUseCase.invoke() } returns flowOf(homeData)

        val viewModel = HomeViewModel(getHomeDataUseCase)

        val job = backgroundScope.launch {
            viewModel.state.collect { }
        }

        advanceUntilIdle()
        val before = viewModel.state.value

        viewModel.onAction(HomeAction.OnFundamentalsClick)
        viewModel.onAction(HomeAction.OnScenariosClick)
        viewModel.onAction(HomeAction.OnProfileClick)
        advanceUntilIdle()

        val after = viewModel.state.value
        assertEquals(before, after)

        job.cancel()
    }

    private fun sampleHomeData(lessonId: String = "lesson_of_the_day") = HomeData(
        lessonOfTheDay = Lesson(
            id = lessonId,
            title = "Lesson of the day",
            category = LessonCategory.FUNDAMENTALS,
            shortDescription = "",
            thumbnailImage = "",
            steps = emptyList()
        ),
        fundamentalsLessons = listOf(
            LessonWithStatus(
                lesson = Lesson(
                    id = "fundamentals_1",
                    title = "Fundamentals 1",
                    category = LessonCategory.FUNDAMENTALS,
                    shortDescription = "",
                    thumbnailImage = "",
                    steps = emptyList()
                ),
                status = LessonStatus.NOT_STARTED
            )
        ),
        scenariosLessons = emptyList(),
        userProgress = UserProgress(),
        currentMission = Mission(
            id = "mission_1",
            title = "Mission 1",
            requiredLessonIds = emptyList()
        )
    )
}