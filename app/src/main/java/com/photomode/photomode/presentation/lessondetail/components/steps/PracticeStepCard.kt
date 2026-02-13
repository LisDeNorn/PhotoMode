package com.photomode.photomode.presentation.lessondetail.components.steps

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.photomode.domain.model.LessonStep

@Composable
fun PracticeStepCard(
    step: LessonStep.Practice,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🎯 ${step.title?.takeIf { it.isNotBlank() } ?: "Практика"}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = step.task,
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = { /* TODO: Запустить тренажер */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Начать практику")
                }
            }
        }
    }
}