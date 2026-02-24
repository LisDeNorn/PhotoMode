package com.photomode.photomode.presentation.lessondetail.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.photomode.domain.model.LessonStep
import com.photomode.photomode.presentation.lessondetail.components.steps.InstructionStepCard
import com.photomode.photomode.presentation.lessondetail.components.steps.PracticeStepCard
import com.photomode.photomode.presentation.lessondetail.components.steps.TheoryStepCard
import kotlinx.coroutines.delay

private const val IMAGES_AUTO_HIDE_DELAY_MS = 4000L

@Composable
fun LessonStepContent(
    step: LessonStep,
    onStartPractice: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isExpanded by remember(step) { mutableStateOf(false) }

    LaunchedEffect(isExpanded) {
        if (!isExpanded) return@LaunchedEffect
        delay(IMAGES_AUTO_HIDE_DELAY_MS)
        isExpanded = false
    }

    when (step) {

        is LessonStep.Theory -> {
            TheoryStepCard(
                step = step,
                isImagesExpanded = isExpanded,
                onImagesTap = { isExpanded = !isExpanded },
                onDismissRequest = { isExpanded = false },
                modifier = modifier
            )
        }

        is LessonStep.Instruction -> {
            InstructionStepCard(
                step = step,
                isImageExpanded = isExpanded,
                onImageTap = { isExpanded = !isExpanded },
                onDismissRequest = { isExpanded = false },
                modifier = modifier
            )
        }

        is LessonStep.Practice -> {
            PracticeStepCard(
                step = step,
                onStartPractice = onStartPractice,
                modifier = modifier
            )
        }
    }
}

