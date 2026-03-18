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

    override suspend fun getCurrentMission(): Mission? {
        context.assets.open("missions.json").use { inputStream ->
            return storage.loadCurrentMission(inputStream)
        }
    }
}

