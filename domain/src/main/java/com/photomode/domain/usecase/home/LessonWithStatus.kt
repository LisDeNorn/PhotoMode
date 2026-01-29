package com.photomode.domain.usecase.home

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonStatus

/**
 * DTO (Data Transfer Object) для передачи урока с его статусом
 * между Domain и Presentation слоями.
 * 
 * Используется для отображения уроков на главном экране с учетом их статуса:
 * - REQUIRED_FOR_MISSION - урок необходим для выполнения текущей миссии
 * - NOT_STARTED - обычный непройденный урок
 * - COMPLETED - пройденный урок
 * 
 * Принцип Clean Architecture: это не чистая доменная модель,
 * а структура данных для представления, поэтому находится в пакете usecase/home,
 * а не в model.
 */
data class LessonWithStatus(
    val lesson: Lesson,
    val status: LessonStatus
)




