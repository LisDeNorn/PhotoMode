package com.photomode.data.repositoryImpl

import com.photomode.domain.model.Mission
import com.photomode.domain.repository.MissionRepository

/** MissionRepository implementation. Currently returns hardcoded mission; can be replaced with DataStore or API. */
class MissionRepositoryImpl : MissionRepository {

    override suspend fun getCurrentMission(): Mission? {
        return Mission(
            id = "mission_1",
            title = "Сфоткать любой ценой",
            requiredLessonIds = listOf(
                "fundamentals_angle",
                "scenarios_cafe_portrait"
            )
        )
    }
}

