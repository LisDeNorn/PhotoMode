package com.photomode.photomode.presentation.lessondetail

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.photomode.data.storage.LocalLessonStorage
import com.photomode.domain.model.Lesson
import com.photomode.photomode.R
import com.photomode.photomode.presentation.components.ErrorView
import com.photomode.photomode.presentation.components.LoadingView
import com.photomode.photomode.presentation.lessondetail.components.LessonCompletionDialog
import com.photomode.photomode.presentation.lessondetail.components.LessonNavigationButtons
import com.photomode.photomode.presentation.lessondetail.components.LessonProgress
import com.photomode.photomode.presentation.lessondetail.components.LessonStepContent
import com.photomode.photomode.presentation.lessondetail.components.LessonTopBar
import com.photomode.photomode.ui.theme.PhotoModeTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    state: LessonDetailUiState,
    onAction: (LessonDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val practiceComingSoonMessage = stringResource(R.string.practice_coming_soon)

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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

                    state.currentStep != null -> state.currentStep?.let { step ->
                        Column(modifier = Modifier.fillMaxSize()) {
                            LessonProgress(
                                stepNumber = state.stepNumber,
                                stepsCount = state.stepsCount,
                                modifier = Modifier
                                    .padding(horizontal = 24.dp)
                                    .padding(top = 12.dp, bottom = 8.dp)
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                            ) {
                                LessonStepContent(
                                    step = step,
                                    onStartPractice = {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(practiceComingSoonMessage)
                                        }
                                        onAction(LessonDetailAction.StartPractice)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            LessonNavigationButtons(
                                canGoBack = state.canGoBack,
                                isLastStep = !state.canGoNext,
                                onBack = { onAction(LessonDetailAction.PrevStep) },
                                onNext = { onAction(LessonDetailAction.NextStep) },
                                onComplete = { onAction(LessonDetailAction.CompleteLesson) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                            )
                        }
                    }
                }

                if (state.showCompletionDialog) {
                    LessonCompletionDialog(
                        onExit = { onAction(LessonDetailAction.ExitLesson) }
                    )
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
                LessonDetailUiState(
                    lessonTitle = lightLesson.title,
                    lessonDescription = lightLesson.shortDescription,
                    steps = lightLesson.steps,
                    currentStepIndex = 0,
                    isLoading = false,
                    isLessonCompleted = false
                )
            } else {
                LessonDetailUiState(isLoading = true)
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
                LessonDetailUiState(
                    lessonTitle = lightLesson.title,
                    lessonDescription = lightLesson.shortDescription,
                    steps = lightLesson.steps,
                    currentStepIndex = 1,
                    isLoading = false,
                    isLessonCompleted = false
                )
            } else {
                LessonDetailUiState(isLoading = true)
            },
            onAction = {}
        )
    }
}

private fun loadLightLessonFromAssets(context: Context): Lesson? {
    return try {
        val storage = LocalLessonStorage()
        val inputStream = context.assets.open("lessons.json")
        val lessons = storage.loadLessonsFromAssets(inputStream)

        val lightLesson = lessons.find { it.id == "fundamentals_light" }

        lightLesson ?: lessons.firstOrNull()
    } catch (e: Exception) {
        null
    }
}
