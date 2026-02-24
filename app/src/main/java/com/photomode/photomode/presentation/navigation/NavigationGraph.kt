package com.photomode.photomode.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.photomode.photomode.presentation.home.HomeRoute
import com.photomode.photomode.presentation.lessonslist.LessonsListRoute
import com.photomode.photomode.presentation.lessondetail.LessonDetailRoute
import com.photomode.domain.model.LessonCategory

/** Centralized route definitions. */
object Routes {
    const val HOME = "home"
    const val LESSONS_LIST = "lessonsList"
    const val LESSON_DETAIL = "lessonDetail"
    const val PROFILE = "profile"
    
    fun lessonsList(category: LessonCategory) = "$LESSONS_LIST/${category.name}"
    fun lessonDetail(lessonId: String) = "$LESSON_DETAIL/$lessonId"
}

/** App navigation graph. Each route creates its own ViewModel. */
@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeRoute(navController = navController)
        }

        composable(
            route = "${Routes.LESSONS_LIST}/{category}",
            arguments = listOf(
                navArgument("category") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("category") ?: ""
            val category = try {
                LessonCategory.valueOf(categoryName)
            } catch (e: IllegalArgumentException) {
                LessonCategory.FUNDAMENTALS
            }

            LessonsListRoute(
                navController = navController,
                category = category
            )
        }

        composable(
            route = "${Routes.LESSON_DETAIL}/{lessonId}",
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            LessonDetailRoute(
                navController = navController,
                lessonId = lessonId
            )
        }

        composable(Routes.PROFILE) {
            // TODO: Add ProfileRoute
            Text(
                text = "Profile Screen",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}