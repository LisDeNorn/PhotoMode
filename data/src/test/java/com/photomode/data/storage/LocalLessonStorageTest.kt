package com.photomode.data.storage

import com.photomode.domain.model.LessonCategory
import com.photomode.domain.model.LessonStep
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalLessonStorageTest {

    private val storage = LocalLessonStorage()

    @Test
    fun loadLessonsFromAssets_parsesTheoryInstructionAndPracticeSteps() {
        val json = """
            {
              "lessons": [
                {
                  "id": "fundamentals_light",
                  "title": "Свет",
                  "category": "FUNDAMENTALS",
                  "shortDescription": "Коротко о свете",
                  "thumbnailImage": "light.webp",
                  "steps": [
                    {
                      "type": "THEORY",
                      "title": "Мягкий свет",
                      "description": "Описание",
                      "goodExampleImage": "images/good.webp",
                      "badExampleImage": "images/bad.webp"
                    },
                    {
                      "type": "INSTRUCTION",
                      "title": "Что делать",
                      "text": "Подойди к окну",
                      "exampleImage": "images/example.webp"
                    },
                    {
                      "type": "PRACTICE",
                      "title": "Попробуй сам",
                      "task": "Сделай 3 кадра"
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        val lessons = storage.loadLessonsFromAssets(json.byteInputStream())

        assertEquals(1, lessons.size)
        assertEquals("fundamentals_light", lessons.first().id)
        assertEquals("Свет", lessons.first().title)
        assertEquals(LessonCategory.FUNDAMENTALS, lessons.first().category)
        assertTrue(lessons.first().steps[0] is LessonStep.Theory)
        assertTrue(lessons.first().steps[1] is LessonStep.Instruction)
        assertTrue(lessons.first().steps[2] is LessonStep.Practice)
    }

    @Test
    fun loadLessonsFromAssets_usesDefaultLabels_whenTheoryLabelsAreMissing() {
        val json = """
            {
              "lessons": [
                {
                  "id": "lesson_1",
                  "title": "Свет",
                  "category": "FUNDAMENTALS",
                  "shortDescription": "Описание",
                  "thumbnailImage": "thumb.webp",
                  "steps": [
                    {
                      "type": "THEORY",
                      "title": "Теория",
                      "description": "Текст",
                      "goodExampleImage": "good.webp",
                      "badExampleImage": "bad.webp"
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        val lessons = storage.loadLessonsFromAssets(json.byteInputStream())
        val theoryStep = lessons.first().steps.first() as LessonStep.Theory

        assertEquals("✅ Хорошо", theoryStep.goodExampleLabel)
        assertEquals("❌ Плохо", theoryStep.badExampleLabel)
    }

    @Test
    fun loadLessonsFromAssets_setsInstructionImageLabelToNull_whenEmpty() {
        val json = """
            {
              "lessons": [
                {
                  "id": "lesson_1",
                  "title": "Свет",
                  "category": "FUNDAMENTALS",
                  "shortDescription": "Описание",
                  "thumbnailImage": "thumb.webp",
                  "steps": [
                    {
                      "type": "INSTRUCTION",
                      "title": "Инструкция",
                      "text": "Сделай шаг",
                      "exampleImage": "example.webp",
                      "exampleImageLabel": ""
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        val lessons = storage.loadLessonsFromAssets(json.byteInputStream())
        val instructionStep = lessons.first().steps.first() as LessonStep.Instruction

        assertNull(instructionStep.exampleImageLabel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun loadLessonsFromAssets_throws_whenStepTypeIsUnknown() {
        val json = """
            {
              "lessons": [
                {
                  "id": "lesson_1",
                  "title": "Свет",
                  "category": "FUNDAMENTALS",
                  "shortDescription": "Описание",
                  "thumbnailImage": "thumb.webp",
                  "steps": [
                    {
                      "type": "UNKNOWN",
                      "title": "Что-то странное"
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        storage.loadLessonsFromAssets(json.byteInputStream())
    }

    @Test(expected = IllegalArgumentException::class)
    fun loadLessonsFromAssets_throws_whenLessonCategoryIsUnknown() {
        val json = """
            {
              "lessons": [
                {
                  "id": "lesson_1",
                  "title": "Свет",
                  "category": "UNKNOWN",
                  "shortDescription": "Описание",
                  "thumbnailImage": "thumb.webp",
                  "steps": [
                    {
                      "type": "PRACTICE",
                      "title": "Практика",
                      "task": "Сделай кадр"
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        storage.loadLessonsFromAssets(json.byteInputStream())
    }

    @Test
    fun loadLessonsFromAssets_parsesMultipleLessons() {
        val json = """
            {
              "lessons": [
                {
                  "id": "lesson_1",
                  "title": "Свет",
                  "category": "FUNDAMENTALS",
                  "shortDescription": "Описание 1",
                  "thumbnailImage": "thumb1.webp",
                  "steps": [
                    {
                      "type": "PRACTICE",
                      "title": "Практика 1",
                      "task": "Сделай кадр"
                    }
                  ]
                },
                {
                  "id": "lesson_2",
                  "title": "Композиция",
                  "category": "FUNDAMENTALS",
                  "shortDescription": "Описание 2",
                  "thumbnailImage": "thumb2.webp",
                  "steps": [
                    {
                      "type": "PRACTICE",
                      "title": "Практика 2",
                      "task": "Сделай второй кадр"
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        val lessons = storage.loadLessonsFromAssets(json.byteInputStream())

        assertEquals(2, lessons.size)
        assertEquals("lesson_1", lessons[0].id)
        assertEquals("lesson_2", lessons[1].id)
        assertEquals(LessonCategory.FUNDAMENTALS, lessons[0].category)
        assertEquals(LessonCategory.FUNDAMENTALS, lessons[1].category)
    }
}