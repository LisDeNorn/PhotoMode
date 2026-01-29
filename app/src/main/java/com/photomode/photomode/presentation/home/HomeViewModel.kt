package com.photomode.photomode.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photmode.data.storage.LocalLessonStorage
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
     * Обрабатывает бизнес-действия пользователя
     * 
     * Принцип разделения:
     * - Бизнес-действия (RefreshData) → обрабатываются здесь
     * - Навигационные действия → обрабатываются в Route, не попадают сюда
     */
    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.RefreshData -> {
                loadHomeData()
            }
            // Навигационные действия (OnLessonClick, OnFundamentalsClick, etc.)
            // обрабатываются в HomeRoute и не передаются в ViewModel
            is HomeAction.OnLessonClick,
            is HomeAction.OnFundamentalsClick,
            is HomeAction.OnScenariosClick,
            is HomeAction.OnProfileClick -> {
                // Игнорируем - навигация обрабатывается в Route
            }
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val homeData = getHomeDataUseCase()
                
                // Вычисляем процент прогресса от общего количества уроков
                // Используем Use Case вместо метода модели (Clean Architecture)
                val totalLessons = homeData.fundamentalsLessons.size + homeData.scenariosLessons.size
                val progressPercentage = calculateProgressPercentageUseCase(
                    userProgress = homeData.userProgress,
                    totalLessons = totalLessons
                )

                _state.value = _state.value.copy(
                    lessonOfTheDay = homeData.lessonOfTheDay,
                    fundamentalsLessons = homeData.fundamentalsLessons,
                    scenariosLessons = homeData.scenariosLessons,
                    userProgress = homeData.userProgress,
                    currentMission = homeData.currentMission,
                    isLoading = false,
                    progressPercentage = progressPercentage
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Ошибка загрузки данных"
                )
            }
        }
    }
}

//class HomeViewModel(
//    private val getLessonOfTheDayUseCase: GetLessonOfTheDayUseCase,
//    private val getFundamentalsLessonsUseCase: GetFundamentalsLessonsUseCase,
//    private val getScenariosLessonsUseCase: GetScenariosLessonsUseCase
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(HomeUiState())
//    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
//
//    // Захардкоженный прогресс (пока)
//    val progressPercentage = 45
//
//    init {
//        loadHomeData()
//    }
//
//    fun loadHomeData() {
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
//
//            try {
//                val lessonOfTheDay = getLessonOfTheDayUseCase()
//                val fundamentalsLessons = getFundamentalsLessonsUseCase(limit = 4)
//                val scenariosLessons = getScenariosLessonsUseCase()
//
//                _uiState.value = _uiState.value.copy(
//                    lessonOfTheDay = lessonOfTheDay,
//                    fundamentalsLessons = fundamentalsLessons,
//                    scenariosLessons = scenariosLessons,
//                    isLoading = false
//                )
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(
//                    isLoading = false,
//                    error = e.message ?: "Ошибка загрузки данных"
//                )
//            }
//        }
//    }
//
//    fun refreshData() {
//        loadHomeData()
//    }
//}

