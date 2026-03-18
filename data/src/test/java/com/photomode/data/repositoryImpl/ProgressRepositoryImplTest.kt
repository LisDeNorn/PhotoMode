package com.photomode.data.repositoryImpl

import com.photomode.data.local.db.dao.CompletedLessonDao
import com.photomode.data.local.db.entity.CompletedLessonEntity
import com.photomode.domain.model.UserProgress
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressRepositoryImplTest {

    @Test
    fun getUserProgress_returnsCompletedLessonIdsAsSet() = runTest {
        val dao = FakeCompletedLessonDao(
            listOf(
                CompletedLessonEntity("lesson_1", 1L),
                CompletedLessonEntity("lesson_1", 2L),
                CompletedLessonEntity("lesson_2", 3L)
            )
        )
        val repository = ProgressRepositoryImpl(dao)

        val result = repository.getUserProgress()

        assertEquals(setOf("lesson_1", "lesson_2"), result.completedLessonIds)
    }

    @Test
    fun getUserProgress_returnsEmptySet_whenDaoIsEmpty() = runTest {
        val dao = FakeCompletedLessonDao()
        val repository = ProgressRepositoryImpl(dao)

        val result = repository.getUserProgress()

        assertEquals(emptySet<String>(), result.completedLessonIds)
    }

    @Test
    fun markLessonAsCompleted_replacesExistingLessonWithoutDuplicates() = runTest {
        val dao = FakeCompletedLessonDao(
            listOf(CompletedLessonEntity("lesson_3", 1L))
        )
        val repository = ProgressRepositoryImpl(dao)

        repository.markLessonAsCompleted("lesson_3")

        val updatedEntity = dao.entities.single { it.lessonId == "lesson_3" }
        assertEquals(1, dao.entities.count { it.lessonId == "lesson_3" })
        assertTrue(updatedEntity.completedAt > 1L)
    }

    @Test
    fun markLessonAsCompleted_addsNewLesson() = runTest {
        val dao = FakeCompletedLessonDao()
        val repository = ProgressRepositoryImpl(dao)

        repository.markLessonAsCompleted("lesson_new")

        assertEquals(1, dao.entities.size)
        assertEquals("lesson_new", dao.entities.first().lessonId)
    }

    @Test
    fun isLessonCompleted_returnsTrue_whenLessonExists() = runTest {
        val dao = FakeCompletedLessonDao(
            listOf(CompletedLessonEntity("lesson_1", 1L))
        )
        val repository = ProgressRepositoryImpl(dao)

        val result = repository.isLessonCompleted("lesson_1")

        assertTrue(result)
    }

    @Test
    fun isLessonCompleted_returnsFalse_whenLessonDoesNotExist() = runTest {
        val dao = FakeCompletedLessonDao(
            listOf(CompletedLessonEntity("lesson_1", 1L))
        )
        val repository = ProgressRepositoryImpl(dao)

        val result = repository.isLessonCompleted("lesson_2")

        assertFalse(result)
    }

    @Test
    fun getUserProgressFlow_mapsDaoFlowToUserProgress() = runTest {
        val dao = FakeCompletedLessonDao()
        val repository = ProgressRepositoryImpl(dao)

        dao.insert(CompletedLessonEntity("lesson_4", 10L))

        val result = repository.getUserProgressFlow().first()

        assertEquals(setOf("lesson_4"), result.completedLessonIds)
    }

    @Test
    fun getUserProgressFlow_returnsEmptySet_whenDaoIsEmpty() = runTest {
        val dao = FakeCompletedLessonDao()
        val repository = ProgressRepositoryImpl(dao)

        val result = repository.getUserProgressFlow().first()

        assertEquals(emptySet<String>(), result.completedLessonIds)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getUserProgressFlow_emitsUpdatedProgress_whenDaoChanges() = runTest {
        val dao = FakeCompletedLessonDao()
        val repository = ProgressRepositoryImpl(dao)
        val collected = mutableListOf<UserProgress>()

        val job = launch {
            repository.getUserProgressFlow()
                .take(2)
                .toList(collected)
        }

        runCurrent()

        dao.insert(CompletedLessonEntity("lesson_5", 20L))
        runCurrent()

        assertEquals(emptySet<String>(), collected[0].completedLessonIds)
        assertEquals(setOf("lesson_5"), collected[1].completedLessonIds)

        job.cancel()
    }

    private class FakeCompletedLessonDao(
        initialEntities: List<CompletedLessonEntity> = emptyList()
    ) : CompletedLessonDao {

        val entities = initialEntities.toMutableList()
        private val idsFlow = MutableStateFlow(entities.map { it.lessonId })

        override suspend fun getCompletedLessonIds(): List<String> {
            return entities.map { it.lessonId }
        }

        override suspend fun insert(entity: CompletedLessonEntity) {
            entities.removeAll { it.lessonId == entity.lessonId }
            entities.add(entity)
            idsFlow.value = entities.map { it.lessonId }
        }

        override fun getCompletedLessonIdsFlow(): Flow<List<String>> {
            return idsFlow
        }
    }
}