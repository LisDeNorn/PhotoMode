package com.photomode.data.repositoryImpl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.photomode.domain.model.UserProgress
import com.photomode.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/** ProgressRepository implementation using DataStore. */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_progress")

class ProgressRepositoryImpl(
    private val context: Context
) : ProgressRepository {
    
    private val completedLessonsKey = stringSetPreferencesKey("completed_lesson_ids")

    override suspend fun getUserProgress(): UserProgress {
        val completedIds = context.dataStore.data
            .first()
            .get(completedLessonsKey) ?: emptySet()
        
        return UserProgress(completedLessonIds = completedIds)
    }
    
    override suspend fun markLessonAsCompleted(lessonId: String) {
        context.dataStore.edit { preferences ->
            val currentIds = preferences[completedLessonsKey] ?: emptySet()
            preferences[completedLessonsKey] = currentIds + lessonId
        }
    }
    
    override suspend fun isLessonCompleted(lessonId: String): Boolean {
        val completedIds = context.dataStore.data
            .first()
            .get(completedLessonsKey) ?: emptySet()
        return completedIds.contains(lessonId)
    }
    
    override suspend fun getCompletedLessonIds(): Set<String> {
        return context.dataStore.data
            .first()
            .get(completedLessonsKey) ?: emptySet()
    }
    
    /** Flow for reactive progress updates (e.g. auto-refresh UI). */
    fun getUserProgressFlow(): Flow<UserProgress> {
        return context.dataStore.data.map { preferences ->
            val completedIds = preferences[completedLessonsKey] ?: emptySet()
            UserProgress(completedLessonIds = completedIds)
        }
    }
}







