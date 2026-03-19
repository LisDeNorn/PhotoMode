package com.photomode.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.photomode.domain.model.AppLocale
import com.photomode.domain.repository.AppLocaleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.appLocaleDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_locale.preferences_pb"
)

private val KEY_APP_LOCALE = stringPreferencesKey("app_locale")

private fun Preferences.toAppLocale(): AppLocale {
    val raw = this[KEY_APP_LOCALE] ?: return AppLocale.RUSSIAN
    if (raw == "SYSTEM") return AppLocale.RUSSIAN
    return runCatching { AppLocale.valueOf(raw) }.getOrDefault(AppLocale.RUSSIAN)
}

/**
 * Persists [AppLocale] as enum name; defaults to [AppLocale.RUSSIAN] when unset.
 */
class AppLocaleRepositoryImpl(
    private val context: Context
) : AppLocaleRepository {

    private val dataStore = context.appLocaleDataStore

    override fun observe(): Flow<AppLocale> {
        return dataStore.data.map { it.toAppLocale() }
    }

    override suspend fun get(): AppLocale {
        return dataStore.data.map { it.toAppLocale() }.first()
    }

    override suspend fun set(locale: AppLocale) {
        dataStore.edit { prefs ->
            prefs[KEY_APP_LOCALE] = locale.name
        }
    }
}
