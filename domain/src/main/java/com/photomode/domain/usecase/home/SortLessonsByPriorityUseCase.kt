package com.photomode.domain.usecase.home

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonStatus
import com.photomode.domain.model.Mission
import com.photomode.domain.usecase.progress.IsLessonCompletedUseCase

/**
 * Use case for sorting lessons by display priority:
 * 1. Required for mission (REQUIRED_FOR_MISSION)
 * 2. Not started (NOT_STARTED)
 * 3. Completed (COMPLETED)
 */
class SortLessonsByPriorityUseCase(
    private val isLessonCompletedUseCase: IsLessonCompletedUseCase
) {
    
    /**
     * Sorts lessons by priority and returns them with status.
     * @param lessons list of lessons to sort
     * @param currentMission current mission (for priority)
     */
    suspend operator fun invoke(
        lessons: List<Lesson>,
        userProgress: com.photomode.domain.model.UserProgress, // Kept for API compatibility
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
    
    /** Determines lesson status from IsLessonCompletedUseCase and current mission. */
    private suspend fun determineStatus(
        lesson: Lesson,
        currentMission: Mission?
    ): LessonStatus {
        return when {
            isLessonCompletedUseCase(lesson.id) -> LessonStatus.COMPLETED
            
            // Not completed but required for mission
            currentMission?.requiredLessonIds?.contains(lesson.id) == true -> 
                LessonStatus.REQUIRED_FOR_MISSION
            
            else -> LessonStatus.NOT_STARTED
        }
    }
    
    /**
     * Applies limit to sorted lessons: mission lessons first, then not started, then completed.
     */
    fun applyLimit(
        sortedLessons: List<LessonWithStatus>,
        limit: Int = 10
    ): List<LessonWithStatus> {
        val forMission = sortedLessons.filter { it.status == LessonStatus.REQUIRED_FOR_MISSION }
        val notStarted = sortedLessons.filter { it.status == LessonStatus.NOT_STARTED }
        val completed = sortedLessons.filter { it.status == LessonStatus.COMPLETED }
        
        val result = mutableListOf<LessonWithStatus>()
        
        result.addAll(forMission.take(limit))
        if (result.size < limit) {
            val remaining = limit - result.size
            result.addAll(notStarted.take(remaining))
        }
        if (result.size < limit) {
            val remaining = limit - result.size
            result.addAll(completed.take(remaining))
        }
        
        return result
    }
}


