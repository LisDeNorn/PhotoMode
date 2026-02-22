package com.photomode.photomode.presentation.lessonslist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.photomode.domain.model.LessonCategory
import com.photomode.photomode.presentation.navigation.Routes
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LessonsListRoute(
    navController: NavController,
    category: LessonCategory
) {
    val viewModel: LessonsListViewModel = koinViewModel(
        parameters = { parametersOf(category) }
    )

    val state by viewModel.state.collectAsState()

    LessonsListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is LessonsListAction.OnLessonClick -> {
                    navController.navigate(Routes.lessonDetail(action.lessonId))
                }
                is LessonsListAction.OnBackClick -> {
                    navController.popBackStack()
                }
                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )
}

