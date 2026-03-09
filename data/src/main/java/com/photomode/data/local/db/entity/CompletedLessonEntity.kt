package com.photomode.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_lessons")
data class CompletedLessonEntity(
    @PrimaryKey
    val lessonId: String,
    val completedAt: Long
)