package com.photomode.domain.repository

import com.photomode.domain.model.AppLocale
import kotlinx.coroutines.flow.Flow

/**
 * Persists the user's in-app language preference (separate from system locale when overridden).
 */
interface AppLocaleRepository {

    fun observe(): Flow<AppLocale>

    suspend fun get(): AppLocale

    suspend fun set(locale: AppLocale)
}
