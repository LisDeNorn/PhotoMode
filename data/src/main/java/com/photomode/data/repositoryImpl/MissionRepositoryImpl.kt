package com.photomode.data.repositoryImpl

import android.content.Context
import com.photomode.data.storage.LocalMissionStorage
import com.photomode.domain.model.Mission
import com.photomode.domain.repository.MissionRepository

/** MissionRepository implementation backed by local seed data in assets. */
class MissionRepositoryImpl(
    private val context: Context
) : MissionRepository {

    private val storage = LocalMissionStorage()

    /** Parsed mission from assets; read once per process like [LessonRepositoryImpl]. */
    private var cachedMission: Mission? = null
    private var missionLoaded: Boolean = false

    override suspend fun getCurrentMission(): Mission? {
        if (missionLoaded) return cachedMission
        context.assets.open("missions.json").use { inputStream ->
            cachedMission = storage.loadCurrentMission(inputStream)
            missionLoaded = true
            return cachedMission
        }
    }
}

