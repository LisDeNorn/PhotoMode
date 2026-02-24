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

    val viewModel: LessonDetailViewModel = koinViewModel(
        parameters = { parametersOf(lessonId) }
    )

    val state by viewModel.state.collectAsState()

    LessonDetailScreen(
        state = state,
        onAction = { action ->
            when (action) {

                is LessonDetailAction.ExitLesson -> {
                    navController.popBackStack()
                }

                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )
}


