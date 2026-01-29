package com.photomode.domain.usecase.home

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonStatus
import com.photomode.domain.model.Mission
import com.photomode.domain.repository.ProgressRepository

/**
 * Use Case для сортировки уроков по приоритету отображения
 * 
 * Приоритет:
 * 1. Непройденные для миссии (REQUIRED_FOR_MISSION)
 * 2. Непройденные обычные (NOT_STARTED)
 * 3. Пройденные (COMPLETED)
 * 
 * Принцип Clean Architecture: использует ProgressRepository для проверки статуса урока,
 * а не методы модели UserProgress.
 */
class SortLessonsByPriorityUseCase(
    private val progressRepository: ProgressRepository
) {
    
    /**
     * Сортирует уроки по приоритету и возвращает с их статусами
     * 
     * @param lessons список уроков для сортировки
     * @param currentMission текущая миссия (для определения приоритета)
     */
    suspend operator fun invoke(
        lessons: List<Lesson>,
        userProgress: com.photomode.domain.model.UserProgress, // Оставляем для совместимости, но не используем
        currentMission: Mission?
    ): List<LessonWithStatus> {
        return lessons.map { lesson ->
            val status = determineStatus(lesson, currentMission)
            LessonWithStatus(lesson = lesson, status = status)
        }.sortedBy { lessonWithStatus ->
            when (lessonWithStatus.status) {
                LessonStatus.REQUIRED_FOR_MISSION -> 1
                LessonStatus.NOT_STARTED -> 2
                LessonStatus.COMPLETED -> 3
            }
        }
    }
    
    /**
     * Определяет статус урока
     * 
     * Использует ProgressRepository для проверки статуса урока,
     * что соответствует принципам Clean Architecture.
     */
    private suspend fun determineStatus(
        lesson: Lesson,
        currentMission: Mission?
    ): LessonStatus {
        return when {
            // Пройденный - проверяем через Repository
            progressRepository.isLessonCompleted(lesson.id) -> LessonStatus.COMPLETED
            
            // Непройденный, но нужен для миссии
            currentMission?.requiredLessonIds?.contains(lesson.id) == true -> 
                LessonStatus.REQUIRED_FOR_MISSION
            
            // Обычный непройденный
            else -> LessonStatus.NOT_STARTED
        }
    }
    
    /**
     * Применяет лимит к отсортированным урокам
     * 
     * Логика:
     * - Сначала берем уроки для миссии
     * - Если не хватило до лимита - добавляем обычные непройденные
     * - Если все еще не хватило - добавляем пройденные
     */
    fun applyLimit(
        sortedLessons: List<LessonWithStatus>,
        limit: Int = 10
    ): List<LessonWithStatus> {
        val forMission = sortedLessons.filter { it.status == LessonStatus.REQUIRED_FOR_MISSION }
        val notStarted = sortedLessons.filter { it.status == LessonStatus.NOT_STARTED }
        val completed = sortedLessons.filter { it.status == LessonStatus.COMPLETED }
        
        val result = mutableListOf<LessonWithStatus>()
        
        // 1. Добавляем уроки для миссии
        result.addAll(forMission.take(limit))
        
        // 2. Если не хватило - добавляем обычные непройденные
        if (result.size < limit) {
            val remaining = limit - result.size
            result.addAll(notStarted.take(remaining))
        }
        
        // 3. Если все еще не хватило - добавляем пройденные
        if (result.size < limit) {
            val remaining = limit - result.size
            result.addAll(completed.take(remaining))
        }
        
        return result
    }
}


