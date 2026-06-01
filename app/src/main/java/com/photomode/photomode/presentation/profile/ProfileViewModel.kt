package com.photomode.photomode.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photomode.domain.repository.ContentSyncResult
import com.photomode.domain.usecase.content.SyncContentUseCase
import com.photomode.domain.usecase.lesson.GetFundamentalsLessonsUseCase
import com.photomode.domain.usecase.lesson.GetLessonByIdUseCase
import com.photomode.domain.usecase.lesson.GetScenariosLessonsUseCase
import com.photomode.domain.usecase.locale.GetAppLocaleUseCase
import com.photomode.domain.usecase.mission.GetCurrentMissionUseCase
import com.photomode.domain.usecase.progress.GetUserProgressUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getFundamentalsLessonsUseCase: GetFundamentalsLessonsUseCase,
    private val getScenariosLessonsUseCase: GetScenariosLessonsUseCase,
    private val getUserProgressUseCase: GetUserProgressUseCase,
    private val getCurrentMissionUseCase: GetCurrentMissionUseCase,
    private val getLessonByIdUseCase: GetLessonByIdUseCase,
    private val getAppLocaleUseCase: GetAppLocaleUseCase,
    private val syncContentUseCase: SyncContentUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState(isLoading = true))
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.RefreshData -> loadProfile()
            ProfileAction.OnBackClick -> Unit
            ProfileAction.OnContinueMissionClick -> Unit
            ProfileAction.OnUpdateContentClick -> updateContent()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val fundamentals = getFundamentalsLessonsUseCase(limit = Int.MAX_VALUE)
                val scenarios = getScenariosLessonsUseCase()
                val totalLessons = fundamentals.size + scenarios.size
                val userProgress = getUserProgressUseCase()
                val currentMission = getCurrentMissionUseCase()
                val completedMissionLessons = currentMission
                    ?.requiredLessonIds
                    ?.count { lessonId -> lessonId in userProgress.completedLessonIds }
                    ?: 0
                val nextMissionLessonId = currentMission
                    ?.requiredLessonIds
                    ?.firstOrNull { lessonId -> lessonId !in userProgress.completedLessonIds }
                    ?.takeIf { lessonId -> getLessonByIdUseCase(lessonId) != null }

                _state.update {
                    it.copy(
                        completedLessons = userProgress.completedLessonIds.size,
                        totalLessons = totalLessons,
                        currentMission = currentMission,
                        completedMissionLessons = completedMissionLessons,
                        totalMissionLessons = currentMission?.requiredLessonIds?.size ?: 0,
                        nextMissionLessonId = nextMissionLessonId,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load profile"
                    )
                }
            }
        }
    }

    private var contentSyncJob: Job? = null

    fun updateContent() {
        if (contentSyncJob?.isActive == true) return

        contentSyncJob = viewModelScope.launch {
            val locale = getAppLocaleUseCase()
            val result = syncContentUseCase(locale)

            Log.d(TAG, "Content sync result: $result")

            if (result is ContentSyncResult.Updated) {
                loadProfile()
            }
        }
    }

    private companion object {
        const val TAG = "ProfileViewModel"
    }
}
