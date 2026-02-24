package com.photomode.domain.repository

import com.photomode.domain.model.Mission

/** Repository for user missions. */
interface MissionRepository {
    suspend fun getCurrentMission(): Mission?
}





