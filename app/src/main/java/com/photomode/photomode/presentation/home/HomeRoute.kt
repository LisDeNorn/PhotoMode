package com.photomode.photomode.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.photomode.photomode.presentation.navigation.Routes
import com.photomode.domain.model.LessonCategory
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute(
    navController: NavController
) {
    val viewModel: HomeViewModel = koinViewModel()

    val state by viewModel.state.collectAsState()

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

                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )
}