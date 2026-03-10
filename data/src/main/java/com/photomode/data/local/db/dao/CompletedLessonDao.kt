package com.photomode.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.photomode.data.local.db.entity.CompletedLessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletedLessonDao {

    @Query("SELECT lessonId FROM completed_lessons")
    suspend fun getCompletedLessonIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CompletedLessonEntity)

    @Query("SELECT lessonId FROM completed_lessons")
    fun getCompletedLessonIdsFlow(): Flow<List<String>>
}