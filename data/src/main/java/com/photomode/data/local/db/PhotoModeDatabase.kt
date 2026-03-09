package com.photomode.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.photomode.data.local.db.dao.CompletedLessonDao
import com.photomode.data.local.db.entity.CompletedLessonEntity

@Database(
    version = 1,
    entities = [CompletedLessonEntity::class]
)

abstract class PhotoModeDatabase : RoomDatabase() {
    abstract fun completedLessonDao(): CompletedLessonDao
}