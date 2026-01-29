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

/**
 * Routes - централизованное хранение маршрутов
 * 
 * Преимущества:
 * - Типобезопасность
 * - Легко рефакторить
 * - Один источник правды
 */
object Routes {
    const val HOME = "home"
    const val LESSONS_LIST = "lessonsList"
    const val LESSON_DETAIL = "lessonDetail"
    const val PROFILE = "profile"
    
    fun lessonsList(category: LessonCategory) = "$LESSONS_LIST/${category.name}"
    fun lessonDetail(lessonId: String) = "$LESSON_DETAIL/$lessonId"
}

/**
 * NavigationGraph - навигационный граф приложения
 * 
 * Принципы:
 * - Не знает про ViewModel
 * - Знает только про Routes
 * - Каждый Route сам создает свой ViewModel
 */
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
            // TODO: Создать ProfileRoute
            Text(
                text = "Profile Screen",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

//sealed class Screen(val route: String) {
//    object Home : Screen("home")
//    data class LessonsList(val category: String) : Screen("lessonsList/{category}") {
//        fun createRoute(category: LessonCategory) = "lessonsList/${category.name}"
//    }
//    data class LessonDetail(val lessonId: String) : Screen("lessonDetail/{lessonId}") {
//        fun createRoute(lessonId: String) = "lessonDetail/$lessonId"
//    }
//    object Profile : Screen("profile")
//}
//
//@Composable
//fun NavigationGraph(
//    navController: NavHostController = rememberNavController()
//) {
//    NavHost(
//        navController = navController,
//        startDestination = Screen.Home.route
//    ) {
//        composable(Screen.Home.route) {
//            HomeScreen(
//                onLessonClick = { lessonId ->
//                    navController.navigate(Screen.LessonDetail(lessonId).createRoute(lessonId))
//                },
//                onFundamentalsClick = {
//                    navController.navigate(Screen.LessonsList("").createRoute(LessonCategory.FUNDAMENTALS))
//                },
//                onScenariosClick = {
//                    navController.navigate(Screen.LessonsList("").createRoute(LessonCategory.SCENARIOS))
//                },
//                onProfileClick = {
//                    navController.navigate(Screen.Profile.route)
//                }
//            )
//        }
//
//        composable(Screen.LessonsList("").route) { backStackEntry ->
//            val categoryName = backStackEntry.arguments?.getString("category") ?: ""
//            val category = try {
//                LessonCategory.valueOf(categoryName)
//            } catch (e: IllegalArgumentException) {
//                LessonCategory.FUNDAMENTALS
//            }
//
//            LessonsListScreen(
//                category = category,
//                onLessonClick = { lessonId ->
//                    navController.navigate(Screen.LessonDetail(lessonId).createRoute(lessonId))
//                },
//                onBackClick = {
//                    navController.popBackStack()
//                }
//            )
//        }
//
//        composable(Screen.LessonDetail("").route) { backStackEntry ->
//            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
//            // TODO: Создать LessonDetailScreen
//            Text(
//                text = "Lesson Detail: $lessonId",
//                modifier = Modifier.padding(16.dp)
//            )
//        }
//
//        composable(Screen.Profile.route) {
//            // TODO: Создать ProfileScreen
//            Text(
//                text = "Profile Screen",
//                modifier = Modifier.padding(16.dp)
//            )
//        }
//    }
//}