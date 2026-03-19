package com.photomode.domain.usecase.home

import com.photomode.domain.usecase.lesson.GetFundamentalsLessonsUseCase
import com.photomode.domain.usecase.lesson.GetLessonOfTheDayUseCase
import com.photomode.domain.usecase.lesson.GetScenariosLessonsUseCase
import com.photomode.domain.usecase.locale.ObserveAppLocaleUseCase
import com.photomode.domain.usecase.mission.GetCurrentMissionUseCase
import com.photomode.domain.usecase.progress.GetUserProgressFlowUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

/**
 * Composite use case: home data reloads when **progress** or **app language** changes.
 * Lessons and mission text come from locale-specific assets via repositories.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetHomeDataUseCase(
    private val getLessonOfTheDayUseCase: GetLessonOfTheDayUseCase,
    private val getFundamentalsLessonsUseCase: GetFundamentalsLessonsUseCase,
    private val getScenariosLessonsUseCase: GetScenariosLessonsUseCase,
    private val getCurrentMissionUseCase: GetCurrentMissionUseCase,
    private val sortLessonsByPriorityUseCase: SortLessonsByPriorityUseCase,
    private val getUserProgressFlowUseCase: GetUserProgressFlowUseCase,
    private val observeAppLocaleUseCase: ObserveAppLocaleUseCase
) {

    operator fun invoke(): Flow<HomeData> =
        combine(
            getUserProgressFlowUseCase(),
            observeAppLocaleUseCase()
        ) { userProgress, _ -> userProgress }
            .flatMapLatest { userProgress ->
                flow {
                    val currentMission = getCurrentMissionUseCase()
                    val allFundamentals = getFundamentalsLessonsUseCase(limit = Int.MAX_VALUE)
                    val allScenarios = getScenariosLessonsUseCase()
                    val allLessons = allFundamentals + allScenarios
                    val lessonOfTheDay = getLessonOfTheDayUseCase(allLessons, userProgress)
                    val sortedFundamentals = sortLessonsByPriorityUseCase(
                        lessons = allFundamentals,
                        userProgress = userProgress,
                        currentMission = currentMission
                    )
                    val sortedScenarios = sortLessonsByPriorityUseCase(
                        lessons = allScenarios,
                        userProgress = userProgress,
                        currentMission = currentMission
                    )
                    val fundamentalsWithLimit = sortLessonsByPriorityUseCase.applyLimit(
                        sortedLessons = sortedFundamentals,
                        limit = 10
                    )
                    val scenariosWithLimit = sortLessonsByPriorityUseCase.applyLimit(
                        sortedLessons = sortedScenarios,
                        limit = 10
                    )
                    emit(
                        HomeData(
                            lessonOfTheDay = lessonOfTheDay,
                            fundamentalsLessons = fundamentalsWithLimit,
                            scenariosLessons = scenariosWithLimit,
                            userProgress = userProgress,
                            currentMission = currentMission
                        )
                    )
                }
            }
}
