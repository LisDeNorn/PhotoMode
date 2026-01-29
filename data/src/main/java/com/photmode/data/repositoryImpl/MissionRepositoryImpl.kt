package com.photmode.data.repositoryImpl

import com.photomode.domain.model.Mission
import com.photomode.domain.repository.MissionRepository

/**
 * Реализация MissionRepository
 * 
 * Пока возвращает захардкоженную миссию.
 * В будущем можно хранить в DataStore или получать с сервера.
 */
class MissionRepositoryImpl : MissionRepository {
    
    override suspend fun getCurrentMission(): Mission? {
        // Пока возвращаем захардкоженную миссию
        // В будущем можно получать из DataStore или с сервера
        return Mission(
            id = "mission_1",
            title = "Сфоткать любой ценой",
            requiredLessonIds = listOf(
                // Уроки, необходимые для выполнения миссии
                "fundamentals_angle",  // Ракурс
                "scenarios_cafe_portrait"  // Портрет в кафе
            )
        )
    }
}

