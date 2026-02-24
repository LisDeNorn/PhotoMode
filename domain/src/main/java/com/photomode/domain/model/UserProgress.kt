package com.photomode.domain.model

/** User progress model — completed lesson IDs. */
data class UserProgress(
    val completedLessonIds: Set<String> = emptySet()
)




