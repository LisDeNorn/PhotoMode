package com.photomode.photomode.presentation.lessondetail

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.photmode.data.storage.LocalLessonStorage
import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonCategory
import com.photomode.domain.model.LessonStep
import com.photomode.photomode.presentation.components.*
import com.photomode.photomode.presentation.lessondetail.components.*
import com.photomode.photomode.ui.theme.PhotoModeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    state: LessonDetailUiState,
    onAction: (LessonDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary, // Синий фон
        topBar = {
            LessonTopBar(
                title = state.lessonTitle,
                onExit = { onAction(LessonDetailAction.ExitLesson) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                when {
                    state.isLoading -> LoadingView()

                    state.error != null -> ErrorView(
                        message = state.error,
                        onRetry = { onAction(LessonDetailAction.RefreshData) }
                    )

                    state.currentStep != null -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            LessonProgress(
                                stepNumber = state.stepNumber,
                                stepsCount = state.stepsCount,
                                modifier = Modifier
                                    .padding(horizontal = 24.dp)
                                    .padding(top = 12.dp, bottom = 8.dp)
                            )

                            // Прокручиваемый контент
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 24.dp, vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                // Шаг
                                LessonStepContent(step = state.currentStep)

                                // Успешное завершение
                                if (state.isLessonCompleted) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            "✅ Урок пройден!",
                                            modifier = Modifier.padding(16.dp),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Фиксированные кнопки навигации внизу
                            LessonNavigationButtons(
                                canGoBack = state.canGoBack,
                                isLastStep = state.stepNumber == state.stepsCount,
                                onBack = { onAction(LessonDetailAction.PrevStep) },
                                onNext = { onAction(LessonDetailAction.NextStep) },
                                onComplete = { onAction(LessonDetailAction.CompleteLesson) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                            )
                        }

                        // Диалог завершения
                        if (state.showCompletionDialog) {
                            AlertDialog(
                                onDismissRequest = { /* Не закрываем по клику мимо */ },
                                title = { Text("Ееее, супер!") },
                                text = { Text("Вы успешно завершили урок и стали на шаг ближе к крутым фотографиям!") },
                                confirmButton = {
                                    Button(
                                        onClick = { onAction(LessonDetailAction.ExitLesson) }
                                    ) {
                                        Text("К миссии")
                                    }
                                },
                                shape = RoundedCornerShape(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Lesson Detail - Light Lesson (Step 1)")
@Composable
private fun LessonDetailScreenPreview() {
    PhotoModeTheme {
        val context = LocalContext.current
        val lightLesson = remember {
            loadLightLessonFromAssets(context)
        }

        LessonDetailScreen(
            state = if (lightLesson != null) {
                val stepsCount = lightLesson.steps.size
                val isLastStep = stepsCount == 1
                LessonDetailUiState(
                    lessonTitle = lightLesson.title,
                    lessonDescription = lightLesson.shortDescription,
                    stepNumber = 1,
                    stepsCount = stepsCount,
                    currentStep = lightLesson.steps.firstOrNull(),
                    canGoBack = false,
                    canGoNext = !isLastStep,
                    canCompleteLesson = isLastStep,
                    isLoading = false,
                    error = null,
                    isLessonCompleted = false
                )
            } else {
                LessonDetailUiState(
                    isLoading = true,
                    error = null
                )
            },
            onAction = {}
        )
    }
}

@Preview(showBackground = true, name = "Lesson Detail - Light Lesson (Step 2)")
@Composable
private fun LessonDetailScreenStep2Preview() {
    PhotoModeTheme {
        val context = LocalContext.current
        val lightLesson = remember {
            loadLightLessonFromAssets(context)
        }

        LessonDetailScreen(
            state = if (lightLesson != null && lightLesson.steps.size >= 2) {
                val stepsCount = lightLesson.steps.size
                val currentStepIndex = 1 // 0-based, so 1 = second step
                val isLastStep = currentStepIndex == stepsCount - 1
                LessonDetailUiState(
                    lessonTitle = lightLesson.title,
                    lessonDescription = lightLesson.shortDescription,
                    stepNumber = currentStepIndex + 1, // 1-based for display
                    stepsCount = stepsCount,
                    currentStep = lightLesson.steps.getOrNull(currentStepIndex),
                    canGoBack = true,
                    canGoNext = !isLastStep,
                    canCompleteLesson = isLastStep,
                    isLoading = false,
                    error = null,
                    isLessonCompleted = false
                )
            } else {
                LessonDetailUiState(
                    isLoading = true,
                    error = null
                )
            },
            onAction = {}
        )
    }
}

/**
 * Загружает реальный урок про свет из assets/lessons.json
 */
private fun loadLightLessonFromAssets(context: Context): Lesson? {
    return try {
        val storage = LocalLessonStorage()
        val inputStream = context.assets.open("lessons.json")
        val lessons = storage.loadLessonsFromAssets(inputStream)

        // Ищем урок про свет по ID
        val lightLesson = lessons.find { it.id == "fundamentals_light" }

        // Если не нашли, возвращаем первый урок (для отладки)
        lightLesson ?: lessons.firstOrNull()
    } catch (e: Exception) {
        // Если не удалось загрузить, возвращаем null
        null
    }
}
