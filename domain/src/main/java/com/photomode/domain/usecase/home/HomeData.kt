package com.photomode.domain.usecase.home

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.Mission
import com.photomode.domain.model.UserProgress

/**
 * DTO for home screen data between domain and presentation layers.
 */
data class HomeData(
    val lessonOfTheDay: Lesson?,
    val fundamentalsLessons: List<LessonWithStatus>,
    val scenariosLessons: List<LessonWithStatus>,
    val userProgress: UserProgress,
    val currentMission: Mission?
)

