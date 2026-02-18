package com.photomode.photomode.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonCategory
import com.photomode.domain.model.LessonStatus
import com.photomode.domain.model.LessonStep
import com.photomode.domain.model.Mission
import com.photomode.domain.model.UserProgress
import com.photomode.domain.usecase.home.LessonWithStatus
import com.photomode.photomode.R
import com.photomode.photomode.presentation.components.ErrorView
import com.photomode.photomode.presentation.components.LoadingView
import com.photomode.photomode.presentation.home.components.FundamentalsSection
import com.photomode.photomode.presentation.home.components.LessonOfTheDayCard
import com.photomode.photomode.presentation.home.components.ScenariosSection
import com.photomode.photomode.presentation.home.components.TopBar
import com.photomode.photomode.ui.theme.PhotoModeTheme

private val HorizontalPadding = 16.dp
private val VerticalPadding = 12.dp
private const val CardsVisibleInRow = 2.3f

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopBar(
                currentMission = state.currentMission,
                onProfileClick = { onAction(HomeAction.OnProfileClick) }
            )
        }
    ) { paddingValues ->

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingView()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorView(
                        message = state.error,
                        onRetry = { onAction(HomeAction.RefreshData) }
                    )
                }
            }

            else -> {
                val config = LocalConfiguration.current
                val horizontalCardWidth =
                    (config.screenWidthDp.dp - HorizontalPadding * 2) / CardsVisibleInRow

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = HorizontalPadding, vertical = VerticalPadding),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.lessonOfTheDay?.let { lesson ->
                        item {
                            LessonOfTheDayCard(
                                lesson = lesson,
                                onClick = { onAction(HomeAction.OnLessonClick(lesson.id)) }
                            )
                        }
                    }

                    item {
                        SectionCard(
                            title = stringResource(R.string.fundamental),
                            onSeeAllClick = { onAction(HomeAction.OnFundamentalsClick) }
                        ) {
                            FundamentalsSection(
                                lessons = state.fundamentalsLessons,
                                cardWidth = horizontalCardWidth,
                                onLessonClick = { lessonId ->
                                    onAction(HomeAction.OnLessonClick(lessonId))
                                }
                            )
                        }
                    }

                    item {
                        SectionCard(
                            title = stringResource(R.string.scenarios),
                            onSeeAllClick = { onAction(HomeAction.OnScenariosClick) }
                        ) {
                            ScenariosSection(
                                lessons = state.scenariosLessons,
                                cardWidth = horizontalCardWidth,
                                onLessonClick = { lessonId ->
                                    onAction(HomeAction.OnLessonClick(lessonId))
                                }
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun SectionCard(
    title: String? = null,
    onSeeAllClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            if (title != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge
                    )

                    if (onSeeAllClick != null) {
                        IconButton(onClick = onSeeAllClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = stringResource(R.string.all_lessons)
                            )
                        }
                    }
                }
            }
            content()
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
}

/**
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
