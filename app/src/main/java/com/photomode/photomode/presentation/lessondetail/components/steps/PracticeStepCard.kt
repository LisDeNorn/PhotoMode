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
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🎯 Практика",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = step.task,
                style = MaterialTheme.typography.bodyLarge
            )

            // TODO: Кнопка для запуска тренажера
            Button(
                onClick = { /* TODO: Запустить тренажер */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Начать практику")
            }
        }
    }
}




