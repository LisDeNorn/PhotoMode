package com.photomode.photomode.presentation.lessonslist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.photomode.photomode.presentation.navigation.Routes
import com.photomode.domain.model.LessonCategory
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * LessonsListRoute - навигационный слой для списка уроков
 * 
 * Ответственность:
 * - Создает ViewModel с зависимостями через Koin
 * - Обрабатывает навигацию
 * - Передает State и Actions в Screen
 */
@Composable
fun LessonsListRoute(
    navController: NavController,
    category: LessonCategory
) {
    // Koin автоматически создает ViewModel с зависимостями
    // parametersOf(category) передает category в ViewModel
    val viewModel: LessonsListViewModel = koinViewModel(
        parameters = { parametersOf(category) }
    )

    val state by viewModel.state.collectAsState()

    LessonsListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                // Навигационные действия обрабатываются здесь
                is LessonsListAction.OnLessonClick -> {
                    navController.navigate(Routes.lessonDetail(action.lessonId))
                }
                is LessonsListAction.OnBackClick -> {
                    navController.popBackStack()
                }
                // Бизнес-действия передаются в ViewModel
                is LessonsListAction.RefreshData -> {
                    viewModel.onAction(action)
                }
            }
        }
    )
}

