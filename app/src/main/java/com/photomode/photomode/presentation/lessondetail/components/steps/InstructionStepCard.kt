package com.photomode.photomode.presentation.lessondetail.components.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.photomode.domain.model.LessonStep
import com.photomode.photomode.presentation.lessondetail.components.InteractiveImage
import com.photomode.photomode.presentation.utils.ImageUtils

@Composable
fun InstructionStepCard(
    step: LessonStep.Instruction,
    isImageExpanded: Boolean,
    onImageTap: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
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
                    if (isImageExpanded) {
                        onDismissRequest()
                    }
                },
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            if (step.exampleImage.isNotEmpty()) {
                InteractiveImage(
                    imageUri = ImageUtils.getAssetImageUri(step.exampleImage),
                    contentDescription = step.title,
                    label = step.exampleImageLabel,
                    isExpanded = isImageExpanded,
                    onTap = onImageTap,
                    borderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            InstructionContent(step.text)
        }
    }

}

private sealed class InstructionBlock {
    data class Paragraph(val text: String) : InstructionBlock()
    data class Bullet(val text: String) : InstructionBlock()
}

@Composable
private fun InstructionContent(text: String) {
    val blocks = remember(text) {
        text.split("\n").mapNotNull { line ->
            val t = line.trim()
            if (t.isEmpty()) return@mapNotNull null
            when {
                t.matches(Regex("^\\d+\\..*")) -> InstructionBlock.Bullet(
                    t.substringAfter(". ").trim()
                )

                t.startsWith("•") || t.startsWith("-") -> InstructionBlock.Bullet(t.drop(1).trim())
                else -> InstructionBlock.Paragraph(t)
            }
        }
    }
    val style = MaterialTheme.typography.bodyLarge
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        blocks.forEach { block ->
            when (block) {
                is InstructionBlock.Paragraph -> Text(text = block.text, style = style)
                is InstructionBlock.Bullet -> Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(6.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                    )
                    Text(text = block.text, style = style, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
