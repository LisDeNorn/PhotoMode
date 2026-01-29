package com.photomode.domain.repository

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonCategory

interface LessonRepository {
    suspend fun getAllLessons(): List<Lesson>
    suspend fun getLessonsByCategory(category: LessonCategory): List<Lesson>

    suspend fun getLessonById(id: String): Lesson?
    suspend fun getLessonOfTheDay(): Lesson?
    suspend fun getFundamentalsLessons(limit: Int = 4): List<Lesson>
    suspend fun getScenariosLessons(): List<Lesson>
}








