package com.photomode.photomode.presentation.lessondetail.components.steps

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.photmode.data.storage.LocalLessonStorage
import com.photomode.domain.model.Lesson
import com.photomode.domain.model.LessonStep
import com.photomode.photomode.presentation.lessondetail.components.InteractiveImage
import com.photomode.photomode.presentation.utils.ImageUtils
import com.photomode.photomode.ui.theme.PhotoModeTheme

@Composable
fun TheoryStepCard(
    step: LessonStep.Theory,
    isImagesExpanded: Boolean,
    onImagesTap: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Text(
            text = step.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    if (isImagesExpanded) {
                        onDismissRequest()
                    }
                },
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onImagesTap
                    ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InteractiveImage(
                    imageUri = ImageUtils.getAssetImageUri(step.badExampleImage),
                    contentDescription = step.badExampleLabel,
                    label = step.badExampleLabel,
                    isExpanded = isImagesExpanded,
                    onTap = onImagesTap,
                    borderColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
                InteractiveImage(
                    imageUri = ImageUtils.getAssetImageUri(step.goodExampleImage),
                    contentDescription = step.goodExampleLabel,
                    label = step.goodExampleLabel,
                    isExpanded = isImagesExpanded,
                    onTap = onImagesTap,
                    borderColor = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }

            val paragraphs = step.description
                .split("\n\n")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            paragraphs.forEach { paragraph ->
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Theory Step - Light Lesson")
@Composable
private fun TheoryStepCardPreview() {
    PhotoModeTheme {
        val context = LocalContext.current
        val lightLesson = remember {
            loadLightLessonFromAssets(context)
        }
        val theoryStep = lightLesson?.steps?.filterIsInstance<LessonStep.Theory>()?.firstOrNull()

        if (theoryStep != null) {
            TheoryStepCard(
                step = theoryStep,
                isImagesExpanded = false,
                onImagesTap = {},
                onDismissRequest = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

private fun loadLightLessonFromAssets(context: Context): Lesson? {
    return try {
        val storage = LocalLessonStorage()
        val inputStream = context.assets.open("lessons.json")
        val lessons = storage.loadLessonsFromAssets(inputStream)
        lessons.find { it.id == "fundamentals_light" } ?: lessons.firstOrNull()
    } catch (e: Exception) {
        null
    }
}