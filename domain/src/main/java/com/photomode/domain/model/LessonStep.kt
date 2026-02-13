package com.photomode.domain.model

// Типы шагов, из которых строится урок.
sealed class LessonStep {
    // 1. ШАГ ТЕОРИИ (сравнение "до/после")
    data class Theory(
        val title: String,
        val description: String,
        val goodExampleImage: String, // URL или ресурс
        val badExampleImage: String,
        val goodExampleLabel: String = "✅ Хорошо", // Подпись для хорошего примера
        val badExampleLabel: String = "❌ Плохо"    // Подпись для плохого примера
    ) : LessonStep()

    // 2. ШАГ ИНСТРУКЦИИ (куда ставить объект)
    data class Instruction(
        val title: String,
        val text: String,
        val exampleImage: String,
        val exampleImageLabel: String? = null
    ) : LessonStep()

    // 3. ШАГ ПРАКТИКИ (ссылка на тренажёр)
    data class Practice(
        val title: String,
        val task: String
    ) : LessonStep()
}