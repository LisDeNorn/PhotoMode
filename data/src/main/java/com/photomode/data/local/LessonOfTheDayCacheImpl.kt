package com.photomode.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.photomode.domain.repository.LessonOfTheDayCache
import kotlinx.coroutines.flow.first

private val Context.lessonOfTheDayDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "lesson_of_the_day.pref"
)

private val KEY_DAY_EPOCH = longPreferencesKey("day_epoch")
private val KEY_LESSON_ID = stringPreferencesKey("lesson_id")

/**
 * DataStore-backed cache so the chosen lesson of the day stays fixed for the whole day.
 */
class LessonOfTheDayCacheImpl(
    private val context: Context
) : LessonOfTheDayCache {

    private val dataStore = context.lessonOfTheDayDataStore

    override suspend fun getCachedLessonIdForDay(dayEpoch: Long): String? {
        val prefs = dataStore.data.first()
        val storedEpoch = prefs[KEY_DAY_EPOCH] ?: return null
        return if (storedEpoch == dayEpoch) prefs[KEY_LESSON_ID] else null
    }

    override suspend fun setCachedLessonForDay(dayEpoch: Long, lessonId: String) {
        dataStore.edit { prefs ->
            prefs[KEY_DAY_EPOCH] = dayEpoch
            prefs[KEY_LESSON_ID] = lessonId
        }
    }
}
