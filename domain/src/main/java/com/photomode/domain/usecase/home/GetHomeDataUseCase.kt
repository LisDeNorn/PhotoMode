package com.photomode.domain.usecase.home

import com.photomode.domain.repository.ProgressRepository
import com.photomode.domain.usecase.lesson.GetFundamentalsLessonsUseCase
import com.photomode.domain.usecase.lesson.GetLessonOfTheDayUseCase
import com.photomode.domain.usecase.lesson.GetScenariosLessonsUseCase
import com.photomode.domain.usecase.mission.GetCurrentMissionUseCase
import com.photomode.domain.usecase.progress.GetUserProgressUseCase

/**
 * Композитный Use Case для загрузки всех данных главного экрана
 * 
 * Использует другие Use Cases для получения данных.
 * Это упрощает ViewModel - вместо 3 Use Cases передается только один.
 * 
 * Преимущества:
 * - Инкапсулирует логику загрузки данных главного экрана
 * - Упрощает ViewModel (меньше зависимостей)
 * - Легче тестировать (можно мокировать один Use Case)
 * - Показывает понимание композиции Use Cases
 */
class GetHomeDataUseCase(
    private val getLessonOfTheDayUseCase: GetLessonOfTheDayUseCase,
    private val getFundamentalsLessonsUseCase: GetFundamentalsLessonsUseCase,
    private val getScenariosLessonsUseCase: GetScenariosLessonsUseCase,
    private val getUserProgressUseCase: GetUserProgressUseCase,
    private val getCurrentMissionUseCase: GetCurrentMissionUseCase,
    private val progressRepository: ProgressRepository
) {
    private val sortLessonsUseCase = SortLessonsByPriorityUseCase(progressRepository)
    
    suspend operator fun invoke(): HomeData {
        val userProgress = getUserProgressUseCase()
        val currentMission = getCurrentMissionUseCase()
        
        val lessonOfTheDay = getLessonOfTheDayUseCase()
        
        // Получаем все уроки по категориям
        val allFundamentals = getFundamentalsLessonsUseCase(limit = Int.MAX_VALUE)
        val allScenarios = getScenariosLessonsUseCase()
        
        // Сортируем по приоритету
        val sortedFundamentals = sortLessonsUseCase(
            lessons = allFundamentals,
            userProgress = userProgress,
            currentMission = currentMission
        )
        
        val sortedScenarios = sortLessonsUseCase(
            lessons = allScenarios,
            userProgress = userProgress,
            currentMission = currentMission
        )
        
        // Применяем лимит 10 карточек с приоритетом
        val fundamentalsWithLimit = sortLessonsUseCase.applyLimit(
            sortedLessons = sortedFundamentals,
            limit = 10
        )
        
        val scenariosWithLimit = sortLessonsUseCase.applyLimit(
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

