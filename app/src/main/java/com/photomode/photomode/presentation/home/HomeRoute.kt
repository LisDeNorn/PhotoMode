package com.photomode.photomode.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.photomode.domain.model.LessonCategory
import com.photomode.photomode.locale.AppLocaleApplier
import com.photomode.photomode.presentation.navigation.Routes
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute(
    navController: NavController
) {
    val viewModel: HomeViewModel = koinViewModel()

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.ApplyAppLocale -> AppLocaleApplier.apply(effect.locale)
            }
        }
    }

    HomeScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is HomeAction.OnLessonClick -> {
                    navController.navigate(Routes.lessonDetail(action.lessonId))
                }

                is HomeAction.OnFundamentalsClick -> {
                    navController.navigate(Routes.lessonsList(LessonCategory.FUNDAMENTALS))
                }

                is HomeAction.OnScenariosClick -> {
                    navController.navigate(Routes.lessonsList(LessonCategory.SCENARIOS))
                }

                is HomeAction.OnProfileClick -> {
                    navController.navigate(Routes.PROFILE)
                }

                is HomeAction.SetAppLocale -> viewModel.onAction(action)

                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )
}