package com.photomode.photomode.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photomode.domain.usecase.lesson.GetFundamentalsLessonsUseCase
import com.photomode.domain.usecase.lesson.GetLessonByIdUseCase
import com.photomode.domain.usecase.lesson.GetScenariosLessonsUseCase
import com.photomode.domain.usecase.mission.GetCurrentMissionUseCase
import com.photomode.domain.usecase.progress.GetUserProgressUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getFundamentalsLessonsUseCase: GetFundamentalsLessonsUseCase,
    private val getScenariosLessonsUseCase: GetScenariosLessonsUseCase,
    private val getUserProgressUseCase: GetUserProgressUseCase,
    private val getCurrentMissionUseCase: GetCurrentMissionUseCase,
    private val getLessonByIdUseCase: GetLessonByIdUseCase
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
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

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

                _state.value = ProfileUiState(
                    completedLessons = userProgress.completedLessonIds.size,
                    totalLessons = totalLessons,
                    currentMission = currentMission,
                    completedMissionLessons = completedMissionLessons,
                    totalMissionLessons = currentMission?.requiredLessonIds?.size ?: 0,
                    nextMissionLessonId = nextMissionLessonId,
                    isLoading = false
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = ProfileUiState(
                    isLoading = false,
                    error = e.message ?: "Failed to load profile"
                )
            }
        }
    }
}
