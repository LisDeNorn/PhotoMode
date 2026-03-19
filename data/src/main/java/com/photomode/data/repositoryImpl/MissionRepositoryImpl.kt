package com.photomode.data.repositoryImpl

import android.content.Context
import com.photomode.data.storage.LocalMissionStorage
import com.photomode.domain.model.AppLocale
import com.photomode.domain.model.Mission
import com.photomode.domain.repository.AppLocaleRepository
import com.photomode.domain.repository.MissionRepository

/** MissionRepository implementation backed by localized JSON in assets. */
class MissionRepositoryImpl(
    private val context: Context,
    private val appLocaleRepository: AppLocaleRepository
) : MissionRepository {

    private val storage = LocalMissionStorage()

    private var cachedMission: Mission? = null
    private var cacheLocale: AppLocale? = null

    override suspend fun getCurrentMission(): Mission? {
        val locale = appLocaleRepository.get()
        if (cacheLocale == locale) {
            return cachedMission
        }
        val assetName = when (locale) {
            AppLocale.RUSSIAN -> "missions_ru.json"
            AppLocale.ENGLISH -> "missions_en.json"
        }
        context.assets.open(assetName).use { inputStream ->
            cachedMission = storage.loadCurrentMission(inputStream)
            cacheLocale = locale
            return cachedMission
        }
    }
}
