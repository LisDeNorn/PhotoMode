package com.photomode.data.content

import android.content.Context
import com.photomode.domain.model.AppLocale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ContentLocalDataSource(
    private val context: Context
) {

    suspend fun getLessonsJson(locale: AppLocale): String {
        return readJson(lessonsFileName(locale))
    }

    suspend fun getMissionsJson(locale: AppLocale): String {
        return readJson(missionsFileName(locale))
    }

    suspend fun saveLessonsJson(locale: AppLocale, json: String) {
        writeCacheFile(lessonsFileName(locale), json)
    }

    suspend fun saveMissionsJson(locale: AppLocale, json: String) {
        writeCacheFile(missionsFileName(locale), json)
    }

    suspend fun clearCachedContent() = withContext(Dispatchers.IO) {
        cacheDir.deleteRecursively()
    }

    private suspend fun readJson(fileName: String): String = withContext(Dispatchers.IO) {
        val cachedFile = File(cacheDir, fileName)

        if (cachedFile.exists()) {
            cachedFile.readText()
        } else {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        }
    }

    private suspend fun writeCacheFile(fileName: String, json: String) = withContext(Dispatchers.IO) {
        cacheDir.mkdirs()
        File(cacheDir, fileName).writeText(json)
    }

    private val cacheDir: File
        get() = File(context.filesDir, CONTENT_CACHE_DIR)

    private fun lessonsFileName(locale: AppLocale): String {
        return when (locale) {
            AppLocale.RUSSIAN -> LESSONS_RU
            AppLocale.ENGLISH -> LESSONS_EN
        }
    }

    private fun missionsFileName(locale: AppLocale): String {
        return when (locale) {
            AppLocale.RUSSIAN -> MISSIONS_RU
            AppLocale.ENGLISH -> MISSIONS_EN
        }
    }

    private companion object {
        const val CONTENT_CACHE_DIR = "content_cache"

        const val LESSONS_RU = "lessons_ru.json"
        const val LESSONS_EN = "lessons_en.json"

        const val MISSIONS_RU = "missions_ru.json"
        const val MISSIONS_EN = "missions_en.json"
    }
}