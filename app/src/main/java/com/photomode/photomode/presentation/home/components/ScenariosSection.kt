package com.photomode.photomode.presentation.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.photomode.domain.model.LessonStatus
import com.photomode.domain.usecase.home.LessonWithStatus
import com.photomode.photomode.presentation.utils.ImageUtils

@Composable
fun ScenariosSection(
    lessons: List<LessonWithStatus>,
    onLessonClick: (String) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Заголовок с кнопкой "Все"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Сценарии",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onSeeAllClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Все сценарии"
                )
            }
        }

        // Карусель с карточками
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            lessons.forEach { lessonWithStatus ->
                ScenarioCard(
                    lessonWithStatus = lessonWithStatus,
                    onClick = { onLessonClick(lessonWithStatus.lesson.id) },
                    modifier = Modifier.width(200.dp)
                )
            }
        }
    }
}

@Composable
private fun ScenarioCard(
    lessonWithStatus: LessonWithStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lesson = lessonWithStatus.lesson
    val status = lessonWithStatus.status
    val context = LocalContext.current
    
    // Определяем стиль в зависимости от статуса
    val borderWidth = when (status) {
        LessonStatus.REQUIRED_FOR_MISSION -> 3.dp  // Яркий контур для миссии
        else -> 1.dp
    }
    
    val borderColor = when (status) {
        LessonStatus.REQUIRED_FOR_MISSION -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }
    
    val alpha = when (status) {
        LessonStatus.COMPLETED -> 0.6f  // Менее яркий для пройденных
        else -> 1f
    }
    
    Card(
        onClick = onClick,
        modifier = modifier
            .alpha(alpha)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Изображение из assets
            AsyncImage(
                model = ImageUtils.getAssetUri(context, lesson.thumbnailImage),
                contentDescription = lesson.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Название
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            
            Spacer(modifier = Modifier.height(4.dp))

            // Описание
            Text(
                text = lesson.shortDescription,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
        }
    }
}

