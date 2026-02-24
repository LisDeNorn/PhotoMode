package com.photomode.domain.model

/** Lesson skeleton filled with content per lesson. */
data class Lesson(
    val id: String,
    val title: String,
    val category: LessonCategory,
    val shortDescription: String,
    val thumbnailImage: String,
    val steps: List<LessonStep>
)