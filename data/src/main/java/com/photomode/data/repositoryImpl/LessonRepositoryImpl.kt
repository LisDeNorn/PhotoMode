package com.photomode.data.repositoryImpl

import android.content.Context
import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonCategory
import com.photomode.domain.repository.LessonRepository
import com.photomode.data.storage.LocalLessonStorage

class LessonRepositoryImpl(
    private val context: Context
) : LessonRepository {

    private val storage = LocalLessonStorage()
    private var cachedLessons: List<Lesson>? = null

    /** Loads lessons from assets and caches them in memory. */
    private fun loadLessons(): List<Lesson> {
        cachedLessons?.let { return it }
        val inputStream = context.assets.open("lessons.json")
        val lessons = storage.loadLessonsFromAssets(inputStream)
        cachedLessons = lessons
        return lessons
    }

    override suspend fun getAllLessons(): List<Lesson> {
        return loadLessons()
    }

    override suspend fun getLessonsByCategory(category: LessonCategory): List<Lesson> {
        return loadLessons().filter { it.category == category }
    }

    override suspend fun getLessonById(id: String): Lesson? {
        return loadLessons().find { it.id == id }
    }

    override suspend fun getFundamentalsLessons(limit: Int): List<Lesson> {
        return loadLessons()
            .filter { it.category == LessonCategory.FUNDAMENTALS }
            .take(limit)
    }

    override suspend fun getScenariosLessons(): List<Lesson> {
        return loadLessons().filter { it.category == LessonCategory.SCENARIOS }
    }
}