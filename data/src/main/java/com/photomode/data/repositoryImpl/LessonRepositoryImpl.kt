package com.photomode.data.repositoryImpl

import android.content.Context
import com.photomode.data.storage.LocalLessonStorage
import com.photomode.domain.model.AppLocale
import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonCategory
import com.photomode.domain.repository.AppLocaleRepository
import com.photomode.domain.repository.LessonRepository

class LessonRepositoryImpl(
    private val context: Context,
    private val appLocaleRepository: AppLocaleRepository
) : LessonRepository {

    private val storage = LocalLessonStorage()
    private var cachedLessons: List<Lesson>? = null
    private var cacheLocale: AppLocale? = null

    private suspend fun loadLessons(): List<Lesson> {
        val locale = appLocaleRepository.get()
        if (cachedLessons != null && cacheLocale == locale) {
            return cachedLessons!!
        }
        val assetName = when (locale) {
            AppLocale.RUSSIAN -> "lessons_ru.json"
            AppLocale.ENGLISH -> "lessons_en.json"
        }
        val inputStream = context.assets.open(assetName)
        val lessons = storage.loadLessonsFromAssets(inputStream)
        cachedLessons = lessons
        cacheLocale = locale
        return lessons
    }

    override suspend fun getAllLessons(): List<Lesson> = loadLessons()

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
