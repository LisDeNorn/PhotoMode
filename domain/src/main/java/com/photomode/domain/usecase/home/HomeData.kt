package com.photomode.domain.usecase.home

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.Mission
import com.photomode.domain.model.UserProgress

/**
 * DTO (Data Transfer Object) для передачи данных главного экрана
 * между Domain и Presentation слоями.
 * 
 * Используется только в контексте главного экрана (home).
 * Не является чистой доменной моделью, а представляет агрегированные данные
 * для конкретного экрана.
 */
data class HomeData(
    val lessonOfTheDay: Lesson?,
    val fundamentalsLessons: List<LessonWithStatus>,  // Уроки с статусами
    val scenariosLessons: List<LessonWithStatus>,    // Уроки с статусами
    val userProgress: UserProgress,
    val currentMission: Mission?  // Текущая миссия
)

