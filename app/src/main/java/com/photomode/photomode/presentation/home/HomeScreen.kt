package com.photomode.photomode.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonCategory
import com.photomode.domain.model.LessonStatus
import com.photomode.domain.model.LessonStep
import com.photomode.domain.model.Mission
import com.photomode.domain.model.UserProgress
import com.photomode.domain.usecase.home.LessonWithStatus
import com.photomode.photomode.presentation.components.*
import com.photomode.photomode.presentation.home.components.*
import com.photomode.photomode.ui.theme.PhotoModeTheme

/**
 * HomeScreen - чистый UI компонент (View в MVVM)
 * 
 * Принципы:
 * - Не знает про ViewModel напрямую
 * - Получает только State и Actions
 * - Легко тестируется
 * - Переиспользуемый
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // TopBar с миссией
        TopBar(
            progressPercentage = state.progressPercentage,
            currentMission = state.currentMission,
            onProfileClick = { onAction(HomeAction.OnProfileClick) }
        )

        when {
            state.isLoading -> LoadingView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            state.error != null -> ErrorView(
                message = state.error ?: "Ошибка",
                onRetry = { onAction(HomeAction.RefreshData) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Урок дня
                    state.lessonOfTheDay?.let { lesson ->
                        LessonOfTheDayCard(
                            lesson = lesson,
                            onClick = { onAction(HomeAction.OnLessonClick(lesson.id)) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    // База
                    FundamentalsSection(
                        lessons = state.fundamentalsLessons,
                        onLessonClick = { lessonId ->
                            onAction(HomeAction.OnLessonClick(lessonId))
                        },
                        onSeeAllClick = { onAction(HomeAction.OnFundamentalsClick) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Сценарии
                    ScenariosSection(
                        lessons = state.scenariosLessons,
                        onLessonClick = { lessonId ->
                            onAction(HomeAction.OnLessonClick(lessonId))
                        },
                        onSeeAllClick = { onAction(HomeAction.OnScenariosClick) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Home Screen - Success")
@Composable
private fun HomeScreenPreview() {
    PhotoModeTheme {
        HomeScreen(
            state = createPreviewHomeState(),
            onAction = {}
        )
    }
}/**
 * Создает моковые данные для превью
 */
private fun createPreviewHomeState(): HomeUiState {
    val mission = Mission(
        id = "mission_1",
        title = "Сфоткать любой ценой",
        requiredLessonIds = listOf("fundamentals_angle", "scenarios_cafe_portrait")
    )
    
    val userProgress = UserProgress(
        completedLessonIds = setOf("fundamentals_light")
    )
    
    val lessonOfTheDay = Lesson(
        id = "scenarios_cafe_portrait",
        title = "Портрет в кафе",
        category = LessonCategory.SCENARIOS,
        shortDescription = "Как сфотографировать девушку в кафе",
        thumbnailImage = "cafe_portrait_thumb.jpg",
        steps = listOf(
            LessonStep.Theory(
                title = "Почему кафе — хорошее место",
                description = "В кафе обычно хорошее освещение",
                goodExampleImage = "cafe_good.jpg",
                badExampleImage = "cafe_bad.jpg"
            )
        )
    )
    
    val fundamentalsLessons = listOf(
        LessonWithStatus(
            lesson = Lesson(
                id = "fundamentals_light",
                title = "Свет",
                category = LessonCategory.FUNDAMENTALS,
                shortDescription = "Как правильно использовать освещение",
                thumbnailImage = "fundamentals_light_thumb.webp",
                steps = emptyList()
            ),
            status = LessonStatus.COMPLETED
        ),
        LessonWithStatus(
            lesson = Lesson(
                id = "fundamentals_horizon",
                title = "Горизонт",
                category = LessonCategory.FUNDAMENTALS,
                shortDescription = "Как выровнять горизонт",
                thumbnailImage = "horizon_thumb.jpg",
                steps = emptyList()
            ),
            status = LessonStatus.NOT_STARTED
        ),
        LessonWithStatus(
            lesson = Lesson(
                id = "fundamentals_angle",
                title = "Ракурс",
                category = LessonCategory.FUNDAMENTALS,
                shortDescription = "Как выбрать правильный угол съемки",
                thumbnailImage = "angle_thumb.jpg",
                steps = emptyList()
            ),
            status = LessonStatus.REQUIRED_FOR_MISSION
        ),
        LessonWithStatus(
            lesson = Lesson(
                id = "fundamentals_frame",
                title = "Кадр",
                category = LessonCategory.FUNDAMENTALS,
                shortDescription = "Как правильно кадрировать фото",
                thumbnailImage = "frame_thumb.jpg",
                steps = emptyList()
            ),
            status = LessonStatus.NOT_STARTED
        )
    )
    
    val scenariosLessons = listOf(
        LessonWithStatus(
            lesson = Lesson(
                id = "scenarios_cafe_portrait",
                title = "Портрет в кафе",
                category = LessonCategory.SCENARIOS,
                shortDescription = "Как сфотографировать девушку в кафе",
                thumbnailImage = "cafe_portrait_thumb.jpg",
                steps = emptyList()
            ),
            status = LessonStatus.REQUIRED_FOR_MISSION
        ),
        LessonWithStatus(
            lesson = Lesson(
                id = "scenarios_group_photo",
                title = "Групповое фото",
                category = LessonCategory.SCENARIOS,
                shortDescription = "Как сфотографировать несколько человек",
                thumbnailImage = "group_photo_thumb.jpg",
                steps = emptyList()
            ),
            status = LessonStatus.NOT_STARTED
        )
    )
    
    return HomeUiState(
        lessonOfTheDay = lessonOfTheDay,
        fundamentalsLessons = fundamentalsLessons,
        scenariosLessons = scenariosLessons,
        userProgress = userProgress,
        currentMission = mission,
        isLoading = false,
        error = null,
        progressPercentage = 45
    )
}