package com.photomode.domain.usecase.mission

import com.photomode.domain.model.Mission
import com.photomode.domain.repository.MissionRepository

/**
 * Use Case для получения текущей миссии
 */
class GetCurrentMissionUseCase(
    private val repository: MissionRepository
) {
    suspend operator fun invoke(): Mission? {
        return repository.getCurrentMission()
    }
}





