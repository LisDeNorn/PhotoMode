package com.photomode.data.repositoryImpl

import com.photomode.data.content.ContentCacheInvalidator
import com.photomode.data.content.ContentLocalDataSource
import com.photomode.data.storage.LocalLessonStorage
import com.photomode.domain.model.AppLocale
import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonCategory
import com.photomode.domain.repository.AppLocaleRepository
import com.photomode.domain.repository.LessonRepository

class LessonRepositoryImpl(
    private val appLocaleRepository: AppLocaleRepository,
    private val contentLocalDataSource: ContentLocalDataSource,
    private val storage: LocalLessonStorage
) : LessonRepository, ContentCacheInvalidator {

    private var cachedLessons: List<Lesson>? = null
    private var cacheLocale: AppLocale? = null

    private suspend fun loadLessons(): List<Lesson> {
        val locale = appLocaleRepository.get()

        if (cachedLessons != null && cacheLocale == locale) {
            return cachedLessons!!
        }

        val json = contentLocalDataSource.getLessonsJson(locale)
        val lessons = storage.loadLessonsFromJson(json)

        cachedLessons = lessons
        cacheLocale = locale

        return lessons
    }

    override fun clearContentCache() {
        cachedLessons = null
        cacheLocale = null
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