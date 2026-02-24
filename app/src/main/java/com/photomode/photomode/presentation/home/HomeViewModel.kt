package com.photomode.photomode.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photomode.domain.usecase.home.GetHomeDataUseCase
import com.photomode.domain.usecase.progress.CalculateProgressPercentageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val calculateProgressPercentageUseCase: CalculateProgressPercentageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadHomeData()
    }

    /**
     * Handles business-related user actions.
     * Navigation events are handled in the Route layer.
     */
    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.RefreshData -> {
                loadHomeData()
            }

            is HomeAction.OnLessonClick,
            HomeAction.OnFundamentalsClick,
            HomeAction.OnScenariosClick,
            HomeAction.OnProfileClick -> Unit
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _state.value = HomeUiState(isLoading = true, error = null)

            try {
                val homeData = getHomeDataUseCase()

                val totalLessons =
                    homeData.fundamentalsLessons.size + homeData.scenariosLessons.size
                val progressPercentage = calculateProgressPercentageUseCase(
                    userProgress = homeData.userProgress,
                    totalLessons = totalLessons
                )
                _state.value = HomeUiState(
                    lessonOfTheDay = homeData.lessonOfTheDay,
                    fundamentalsLessons = homeData.fundamentalsLessons,
                    scenariosLessons = homeData.scenariosLessons,
                    userProgress = homeData.userProgress,
                    currentMission = homeData.currentMission,
                    progressPercentage = progressPercentage,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load home data"
                )
            }
        }
    }
}