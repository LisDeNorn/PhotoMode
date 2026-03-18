package com.photomode.data.storage

import com.photomode.domain.model.Mission
import org.json.JSONObject
import java.io.InputStream

class LocalMissionStorage {

    fun loadCurrentMission(inputStream: InputStream): Mission? {
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val missionJson = jsonObject.optJSONObject("currentMission") ?: return null

        return Mission(
            id = missionJson.getString("id"),
            title = missionJson.getString("title"),
            requiredLessonIds = missionJson.getJSONArray("requiredLessonIds").toStringList()
        )
    }

    private fun org.json.JSONArray.toStringList(): List<String> {
        return buildList(length()) {
            for (index in 0 until length()) {
                add(getString(index))
            }
        }
    }
}
