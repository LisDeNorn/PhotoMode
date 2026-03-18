package com.photomode.photomode.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.photomode.photomode.presentation.navigation.Routes
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileRoute(
    navController: NavController
) {
    val viewModel: ProfileViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    ProfileScreen(
        state = state,
        onAction = { action ->
            when (action) {
                ProfileAction.OnBackClick -> navController.popBackStack()
                ProfileAction.RefreshData -> viewModel.onAction(action)
                ProfileAction.OnContinueMissionClick -> {
                    state.nextMissionLessonId?.let { lessonId ->
                        navController.navigate(Routes.lessonDetail(lessonId))
                    }
                }
            }
        }
    )
}
