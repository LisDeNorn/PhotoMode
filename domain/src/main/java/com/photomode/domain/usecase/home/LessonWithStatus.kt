package com.photomode.domain.usecase.home

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonStatus

/**
 * DTO for passing a lesson with its display status between domain and presentation.
 * Used on the home screen: REQUIRED_FOR_MISSION, NOT_STARTED, COMPLETED.
 */
data class LessonWithStatus(
    val lesson: Lesson,
    val status: LessonStatus
)




