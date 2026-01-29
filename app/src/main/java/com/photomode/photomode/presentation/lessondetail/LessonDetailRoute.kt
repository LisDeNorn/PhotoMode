package com.photomode.photomode.presentation.lessondetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LessonDetailRoute(
    navController: NavController,
    lessonId: String
) {
    // Koin создает ViewModel с параметром lessonId
    val viewModel: LessonDetailViewModel = koinViewModel(
        parameters = { parametersOf(lessonId) }
    )

    val state by viewModel.state.collectAsState()

    LessonDetailScreen(
        state = state,
        onAction = { action ->
            when (action) {
                // Навигационные действия - обрабатываются в Route

                is LessonDetailAction.ExitLesson -> {
                    navController.popBackStack()
                }
                // Бизнес-действия - передаются в ViewModel
                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )
}


