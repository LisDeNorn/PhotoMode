package com.photomode.domain.usecase.home

import com.photomode.domain.usecase.lesson.GetFundamentalsLessonsUseCase
import com.photomode.domain.usecase.lesson.GetLessonOfTheDayUseCase
import com.photomode.domain.usecase.lesson.GetScenariosLessonsUseCase
import com.photomode.domain.usecase.mission.GetCurrentMissionUseCase
import com.photomode.domain.usecase.progress.GetUserProgressUseCase

/**
 * Composite use case that loads all home screen data.
 * Composes other use cases so the ViewModel has a single dependency.
 */
class GetHomeDataUseCase(
    private val getLessonOfTheDayUseCase: GetLessonOfTheDayUseCase,
    private val getFundamentalsLessonsUseCase: GetFundamentalsLessonsUseCase,
    private val getScenariosLessonsUseCase: GetScenariosLessonsUseCase,
    private val getUserProgressUseCase: GetUserProgressUseCase,
    private val getCurrentMissionUseCase: GetCurrentMissionUseCase,
    private val sortLessonsByPriorityUseCase: SortLessonsByPriorityUseCase
) {
    
    suspend operator fun invoke(): HomeData {
        val userProgress = getUserProgressUseCase()
        val currentMission = getCurrentMissionUseCase()
        
        val lessonOfTheDay = getLessonOfTheDayUseCase()
        
        val allFundamentals = getFundamentalsLessonsUseCase(limit = Int.MAX_VALUE)
        val allScenarios = getScenariosLessonsUseCase()
        
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
        
        return HomeData(
            lessonOfTheDay = lessonOfTheDay,
            fundamentalsLessons = fundamentalsWithLimit,
            scenariosLessons = scenariosWithLimit,
            userProgress = userProgress,
            currentMission = currentMission
        )
    }
}

