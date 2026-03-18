package com.photomode.data.storage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class LocalMissionStorageTest {

    private val storage = LocalMissionStorage()

    @Test
    fun loadCurrentMission_parsesMissionFromAssets() {
        val json = """
            {
              "currentMission": {
                "id": "mission_1",
                "title": "Базовый минимум",
                "requiredLessonIds": [
                  "fundamentals_light",
                  "scenarios_cafe_portrait"
                ]
              }
            }
        """.trimIndent()

        val mission = storage.loadCurrentMission(json.byteInputStream())

        assertNotNull(mission)
        assertEquals("mission_1", mission?.id)
        assertEquals("Базовый минимум", mission?.title)
        assertEquals(
            listOf("fundamentals_light", "scenarios_cafe_portrait"),
            mission?.requiredLessonIds
        )
    }

    @Test
    fun loadCurrentMission_returnsNull_whenCurrentMissionIsMissing() {
        val json = """
            {
              "anotherField": {
                "id": "mission_1"
              }
            }
        """.trimIndent()

        val mission = storage.loadCurrentMission(json.byteInputStream())

        assertNull(mission)
    }

    @Test
    fun loadCurrentMission_parsesEmptyRequiredLessonIds() {
        val json = """
            {
              "currentMission": {
                "id": "mission_2",
                "title": "Пустая миссия",
                "requiredLessonIds": []
              }
            }
        """.trimIndent()

        val mission = storage.loadCurrentMission(json.byteInputStream())

        assertNotNull(mission)
        assertEquals("mission_2", mission?.id)
        assertEquals("Пустая миссия", mission?.title)
        assertEquals(emptyList<String>(), mission?.requiredLessonIds)
    }
}