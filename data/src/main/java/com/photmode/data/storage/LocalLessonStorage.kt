package com.photmode.data.storage

import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonCategory
import com.photomode.domain.model.LessonStep
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class LocalLessonStorage {
    
    fun loadLessonsFromAssets(inputStream: InputStream): List<Lesson> {
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val lessonsArray = jsonObject.getJSONArray("lessons")
        
        val lessons = mutableListOf<Lesson>()
        for (i in 0 until lessonsArray.length()) {
            val lessonJson = lessonsArray.getJSONObject(i)
            lessons.add(parseLesson(lessonJson))
        }
        
        return lessons
    }
    
    private fun parseLesson(json: JSONObject): Lesson {
        val id = json.getString("id")
        val title = json.getString("title")
        val category = LessonCategory.valueOf(json.getString("category"))
        val shortDescription = json.getString("shortDescription")
        val thumbnailImage = json.getString("thumbnailImage")
        
        val stepsArray = json.getJSONArray("steps")
        val steps = mutableListOf<LessonStep>()
        for (i in 0 until stepsArray.length()) {
            val stepJson = stepsArray.getJSONObject(i)
            steps.add(parseStep(stepJson))
        }
        
        return Lesson(
            id = id,
            title = title,
            category = category,
            shortDescription = shortDescription,
            thumbnailImage = thumbnailImage,
            steps = steps
        )
    }
    
    private fun parseStep(json: JSONObject): LessonStep {
        val type = json.getString("type")
        
        return when (type) {
            "THEORY" -> {
                LessonStep.Theory(
                    title = json.getString("title"),
                    description = json.getString("description"),
                    goodExampleImage = json.getString("goodExampleImage"),
                    badExampleImage = json.getString("badExampleImage"),
                    goodExampleLabel = json.optString("goodExampleLabel", "✅ Хорошо"),
                    badExampleLabel = json.optString("badExampleLabel", "❌ Плохо")
                )
            }
            "INSTRUCTION" -> {
                LessonStep.Instruction(
                    text = json.getString("text"),
                    exampleImage = json.getString("exampleImage"),
                    exampleImageLabel = json.optString("exampleImageLabel").takeIf { it.isNotEmpty() }
                )
            }
            "PRACTICE" -> {
                LessonStep.Practice(
                    task = json.getString("task")
                )
            }
            else -> throw IllegalArgumentException("Unknown step type: $type")
        }
    }
}








