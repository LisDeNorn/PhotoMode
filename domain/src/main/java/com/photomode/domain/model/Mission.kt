package com.photomode.domain.model

/** User mission: goal and required lesson IDs. */
data class Mission(
    val id: String,
    val title: String,
    val requiredLessonIds: List<String>
)





