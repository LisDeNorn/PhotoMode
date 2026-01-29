package com.photomode.domain.repository

import com.photomode.domain.model.Mission

/**
 * Repository для работы с миссиями
 */
interface MissionRepository {
    /**
     * Получить текущую активную миссию пользователя
     */
    suspend fun getCurrentMission(): Mission?
}





