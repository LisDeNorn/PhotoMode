package com.photomode.photomode.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photomode.domain.usecase.home.GetHomeDataUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val getHomeDataUseCase: GetHomeDataUseCase
) : ViewModel() {

    /** Triggers reload (pull-to-refresh / retry on error). Replays last so no drop when no collector. */
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * Single source of truth: load on first subscribe and on each refresh;
     * map to UI state, handle errors, expose as StateFlow for UI.
     */
    val state: StateFlow<HomeUiState> = merge(flowOf(Unit), refreshTrigger)
        .flatMapLatest { getHomeDataUseCase() }
        .map { homeData ->
            HomeUiState(
                lessonOfTheDay = homeData.lessonOfTheDay,
                fundamentalsLessons = homeData.fundamentalsLessons,
                scenariosLessons = homeData.scenariosLessons,
                userProgress = homeData.userProgress,
                currentMission = homeData.currentMission,
                isLoading = false,
                error = null
            )
        }
        .catch { e ->
            emit(
                HomeUiState(
                    isLoading = false,
                    error = e.message ?: "Failed to load home data"
                )
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            HomeUiState(isLoading = true)
        )

    private companion object {
        /** Cancel upstream when no subscribers for this duration (e.g. user left screen). */
        const val STOP_TIMEOUT_MS = 5_000L
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.RefreshData -> refreshTrigger.tryEmit(Unit)
            is HomeAction.OnLessonClick,
            HomeAction.OnFundamentalsClick,
            HomeAction.OnScenariosClick,
            HomeAction.OnProfileClick -> Unit
        }
    }
}