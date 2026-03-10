package com.photomode.data.repositoryImpl

import com.photomode.data.local.db.dao.CompletedLessonDao
import com.photomode.data.local.db.entity.CompletedLessonEntity
import com.photomode.domain.model.UserProgress
import com.photomode.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** ProgressRepository implementation using Room. */
class ProgressRepositoryImpl(
    private val dao: CompletedLessonDao
) : ProgressRepository {

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


    override fun getUserProgressFlow(): Flow<UserProgress> {
        return dao.getCompletedLessonIdsFlow()
            .map { ids ->
                UserProgress(completedLessonIds = ids.toSet())
            }
    }
}







