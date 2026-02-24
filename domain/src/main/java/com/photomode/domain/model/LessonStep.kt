package com.photomode.domain.model

/** Step types that make up a lesson. */
sealed class LessonStep {
    /** Theory step (before/after comparison). */
    data class Theory(
        val title: String,
        val description: String,
        val goodExampleImage: String,
        val badExampleImage: String,
        val goodExampleLabel: String = "✅ Хорошо",
        val badExampleLabel: String = "❌ Плохо"
    ) : LessonStep()

    /** Instruction step. */
    data class Instruction(
        val title: String,
        val text: String,
        val exampleImage: String,
        val exampleImageLabel: String? = null
    ) : LessonStep()

    /** Practice step (link to trainer). */
    data class Practice(
        val title: String,
        val task: String
    ) : LessonStep()
}