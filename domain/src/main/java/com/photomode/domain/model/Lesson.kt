package com.photomode.domain.model

// Это КАРКАС. Его заполняют конкретным контентом для каждого урока.
data class Lesson(
    val id: String,           // Например: "rule_of_thirds"
    val title: String,        // "Правило третей"
    val category: LessonCategory, // Категория урока
    val shortDescription: String, // Краткое описание для карточек
    val thumbnailImage: String,   // Превью изображение для карточек
    val steps: List<LessonStep> // ПОСЛЕДОВАТЕЛЬНОСТЬ ШАГОВ для этого урока
)