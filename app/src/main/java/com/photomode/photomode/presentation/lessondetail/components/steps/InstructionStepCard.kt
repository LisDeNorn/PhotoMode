package com.photomode.photomode.presentation.lessondetail.components.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import com.photomode.domain.model.LessonStep
import com.photomode.photomode.presentation.lessondetail.components.InteractiveImage
import com.photomode.photomode.presentation.utils.ImageUtils
import kotlinx.coroutines.delay

@Composable
fun InstructionStepCard(
    step: LessonStep.Instruction,
    modifier: Modifier = Modifier
) {
    var dismissImageKey by remember { mutableStateOf(0) }
    var isImageExpanded by remember { mutableStateOf(false) }
    var imageTimerKey by remember { mutableStateOf(0) }
    val hasImageLabel = step.exampleImageLabel != null

    LaunchedEffect(dismissImageKey) {
        isImageExpanded = false
    }
    LaunchedEffect(isImageExpanded, imageTimerKey) {
        if (isImageExpanded) {
            delay(4000)
            isImageExpanded = false
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Инструкция в карточке; тап скрывает подсказку у картинки (если есть)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (hasImageLabel) {
                        Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { dismissImageKey++ }
                    } else Modifier
                ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Заголовок "Инструкция" с иконкой
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Инструкция",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Пронумерованный список инструкций
                parseAndDisplayInstructions(step.text)
            }
        }

        // Пример изображения (если есть)
        if (step.exampleImage.isNotEmpty()) {
            val label = step.exampleImageLabel

            if (label != null) {
                InteractiveImage(
                    imageUri = ImageUtils.getAssetImageUri(step.exampleImage),
                    contentDescription = label,
                    label = label,
                    isExpanded = isImageExpanded,
                    onTap = {
                        isImageExpanded = !isImageExpanded
                        if (isImageExpanded) imageTimerKey++
                    },
                    modifier = Modifier.fillMaxWidth(),
                    height = 300.dp,
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                // Без подсказки: только отрисовка
                InteractiveImage(
                    imageUri = ImageUtils.getAssetImageUri(step.exampleImage),
                    contentDescription = "Пример",
                    label = null,
                    isExpanded = false,
                    onTap = {},
                    modifier = Modifier.fillMaxWidth(),
                    height = 300.dp,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Composable
private fun parseAndDisplayInstructions(text: String) {
    // Парсим текст и извлекаем пронумерованные пункты
    val lines = text.split("\n")
    var instructionNumber = 1
    val instructions = mutableListOf<Pair<Int, String>>()
    val descriptions = mutableListOf<String>()
    
    lines.forEach { line ->
        val trimmedLine = line.trim()
        if (trimmedLine.isNotEmpty()) {
            when {
                trimmedLine.matches(Regex("^\\d+\\..*")) -> {
                    // Пронумерованный пункт (1. текст)
                    val instructionText = trimmedLine.substringAfter(". ").trim()
                    if (instructionText.isNotEmpty()) {
                        instructions.add(instructionNumber++ to instructionText)
                    }
                }
                trimmedLine.startsWith("•") || trimmedLine.startsWith("-") -> {
                    // Маркированный пункт - преобразуем в пронумерованный
                    val instructionText = trimmedLine.substring(1).trim()
                    if (instructionText.isNotEmpty()) {
                        instructions.add(instructionNumber++ to instructionText)
                    }
                }
                else -> {
                    // Обычный текст (описание) - показываем перед инструкциями
                    descriptions.add(trimmedLine)
                }
            }
        }
    }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Сначала показываем описания
        descriptions.forEach { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // Затем показываем пронумерованные инструкции
        instructions.forEach { (number, text) ->
            InstructionItem(
                number = number,
                text = text
            )
        }
    }
}

@Composable
private fun InstructionItem(
    number: Int,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$number.",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

