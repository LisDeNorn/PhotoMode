package com.photomode.data.repositoryImpl

import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.photomode.data.local.db.dao.CompletedLessonDao
import com.photomode.data.local.db.entity.CompletedLessonEntity
import com.photomode.domain.model.UserProgress
import com.photomode.domain.repository.ProgressRepository

/** ProgressRepository implementation using Room. */
class ProgressRepositoryImpl(
    private val dao: CompletedLessonDao
) : ProgressRepository {

    private val completedLessonsKey = stringSetPreferencesKey("completed_lesson_ids")

    override suspend fun getUserProgress(): UserProgress {
        val completedIds = dao.getCompletedLessonIds()
        return UserProgress(completedLessonIds = completedIds.toSet())
    }

    override suspend fun markLessonAsCompleted(lessonId: String) {
        dao.insert(
            CompletedLessonEntity(
                lessonId = lessonId,
                completedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun isLessonCompleted(lessonId: String): Boolean {
        val completedIds = dao.getCompletedLessonIds()
        return completedIds.contains(lessonId)
    }


    /** Flow for reactive progress updates (e.g. auto-refresh UI). */

}







