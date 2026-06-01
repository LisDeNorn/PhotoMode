package com.photomode.data.repositoryImpl

import com.photomode.data.content.ContentCacheInvalidator
import com.photomode.data.content.ContentLocalDataSource
import com.photomode.data.storage.LocalMissionStorage
import com.photomode.domain.model.AppLocale
import com.photomode.domain.model.Mission
import com.photomode.domain.repository.AppLocaleRepository
import com.photomode.domain.repository.MissionRepository

class MissionRepositoryImpl(
    private val appLocaleRepository: AppLocaleRepository,
    private val contentLocalDataSource: ContentLocalDataSource,
    private val storage: LocalMissionStorage
) : MissionRepository, ContentCacheInvalidator {

    private var cachedMission: Mission? = null
    private var cacheLocale: AppLocale? = null

    override suspend fun getCurrentMission(): Mission? {
        val locale = appLocaleRepository.get()

        if (cacheLocale == locale) {
            return cachedMission
        }

        val json = contentLocalDataSource.getMissionsJson(locale)

        cachedMission = storage.loadCurrentMissionFromJson(json)
        cacheLocale = locale

        return cachedMission
    }

    override fun clearContentCache() {
        cachedMission = null
        cacheLocale = null
    }
}