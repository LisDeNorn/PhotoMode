package com.photomode.domain.model

/**
 * Модель миссии пользователя
 * 
 * Миссия определяет цель и список уроков, необходимых для её выполнения.
 */
data class Mission(
    val id: String,
    val title: String,  // Например: "Сфоткать любой ценой"
    val requiredLessonIds: List<String>  // ID уроков, необходимых для миссии
)





