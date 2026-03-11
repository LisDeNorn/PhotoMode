package com.photomode.domain.usecase.home

import com.photomode.domain.usecase.lesson.GetFundamentalsLessonsUseCase
import com.photomode.domain.usecase.lesson.GetLessonOfTheDayUseCase
import com.photomode.domain.usecase.lesson.GetScenariosLessonsUseCase
import com.photomode.domain.usecase.mission.GetCurrentMissionUseCase
import com.photomode.domain.usecase.progress.GetUserProgressFlowUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Composite use case that loads all home screen data as a reactive stream.
 * Loads static data (lessons, mission) once, then maps progress flow to HomeData;
 * each progress update re-sorts and re-emits so UI order updates immediately.
 */
class GetHomeDataUseCase(
    private val getLessonOfTheDayUseCase: GetLessonOfTheDayUseCase,
    private val getFundamentalsLessonsUseCase: GetFundamentalsLessonsUseCase,
    private val getScenariosLessonsUseCase: GetScenariosLessonsUseCase,
    private val getCurrentMissionUseCase: GetCurrentMissionUseCase,
    private val sortLessonsByPriorityUseCase: SortLessonsByPriorityUseCase,
    private val getUserProgressFlowUseCase: GetUserProgressFlowUseCase
) {

    operator fun invoke(): Flow<HomeData> = flow {
        val currentMission = getCurrentMissionUseCase()
        val allFundamentals = getFundamentalsLessonsUseCase(limit = Int.MAX_VALUE)
        val allScenarios = getScenariosLessonsUseCase()
        val allLessons = allFundamentals + allScenarios

        emitAll(
            getUserProgressFlowUseCase().map { userProgress ->
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
                HomeData(
                    lessonOfTheDay = lessonOfTheDay,
                    fundamentalsLessons = fundamentalsWithLimit,
                    scenariosLessons = scenariosWithLimit,
                    userProgress = userProgress,
                    currentMission = currentMission
                )
            }
        )
    }
}

