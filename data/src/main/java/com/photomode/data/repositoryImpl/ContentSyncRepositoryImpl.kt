package com.photomode.data.repositoryImpl

import com.photomode.data.content.ContentCacheInvalidator
import com.photomode.data.content.ContentLocalDataSource
import com.photomode.data.content.ContentRemoteDataSource
import com.photomode.data.storage.LocalLessonStorage
import com.photomode.data.storage.LocalMissionStorage
import com.photomode.domain.model.AppLocale
import com.photomode.domain.repository.ContentSyncRepository
import com.photomode.domain.repository.ContentSyncResult

class ContentSyncRepositoryImpl(
    private val remoteDataSource: ContentRemoteDataSource,
    private val localDataSource: ContentLocalDataSource,
    private val lessonStorage: LocalLessonStorage,
    private val missionStorage: LocalMissionStorage,
    private val cacheInvalidators: List<ContentCacheInvalidator>
) : ContentSyncRepository {

    override suspend fun syncContent(locale: AppLocale): ContentSyncResult {
        return try {
            val lessonsJson = remoteDataSource.getLessonsJson(locale)
            val missionsJson = remoteDataSource.getMissionsJson(locale)

            validateContent(lessonsJson, missionsJson)

            localDataSource.saveLessonsJson(locale, lessonsJson)
            localDataSource.saveMissionsJson(locale, missionsJson)

            cacheInvalidators.forEach { it.clearContentCache() }

            ContentSyncResult.Updated
        } catch (e: Exception) {
            ContentSyncResult.Failed(
                message = e.message ?: "Content sync failed"
            )
        }
    }

    private fun validateContent(
        lessonsJson: String,
        missionsJson: String
    ) {
        val lessons = lessonStorage.loadLessonsFromJson(lessonsJson)
        val mission = missionStorage.loadCurrentMissionFromJson(missionsJson)

        val lessonIds = lessons.map { it.id }

        require(lessons.isNotEmpty()) {
            "Lessons list is empty"
        }

        require(lessonIds.none { it.isBlank() }) {
            "Blank lesson id found"
        }

        require(lessonIds.size == lessonIds.toSet().size) {
            "Duplicate lesson ids found"
        }

        if (mission != null) {
            val missingLessonIds = mission.requiredLessonIds.filterNot { it in lessonIds }

            require(missingLessonIds.isEmpty()) {
                "Mission references missing lessons: $missingLessonIds"
            }
        }
    }
}