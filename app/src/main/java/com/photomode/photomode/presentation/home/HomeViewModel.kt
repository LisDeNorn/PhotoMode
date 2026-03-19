package com.photomode.photomode.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photomode.domain.usecase.home.GetHomeDataUseCase
import com.photomode.domain.usecase.locale.SetAppLocaleUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val setAppLocaleUseCase: SetAppLocaleUseCase
) : ViewModel() {

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private val _effects = Channel<HomeEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
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
        const val STOP_TIMEOUT_MS = 5_000L
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.RefreshData -> refreshTrigger.tryEmit(Unit)
            is HomeAction.SetAppLocale -> {
                viewModelScope.launch {
                    setAppLocaleUseCase(action.locale)
                    _effects.send(HomeEffect.ApplyAppLocale(action.locale))
                }
            }
            is HomeAction.OnLessonClick,
            HomeAction.OnFundamentalsClick,
            HomeAction.OnScenariosClick,
            HomeAction.OnProfileClick -> Unit
        }
    }
}
