package com.photomode.photomode.presentation.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.photomode.domain.model.LessonStatus
import com.photomode.domain.usecase.home.LessonWithStatus
import com.photomode.photomode.presentation.utils.ImageUtils

@Composable
fun HorizontalLessonCard(
    lessonWithStatus: LessonWithStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardWidth: Dp
) {
    val lesson = lessonWithStatus.lesson
    val status = lessonWithStatus.status
    val context = LocalContext.current

    val borderWidth = when (status) {
        LessonStatus.REQUIRED_FOR_MISSION -> 3.dp
        else -> 1.dp
    }
    val borderColor = when (status) {
        LessonStatus.REQUIRED_FOR_MISSION -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }
    val alpha = when (status) {
        LessonStatus.COMPLETED -> 0.6f
        else -> 1f
    }

    Card(
        onClick = onClick,
        modifier = modifier
            .width(cardWidth)
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
                .padding(10.dp)
        ) {
            AsyncImage(
                model = ImageUtils.getAssetUri(context, lesson.thumbnailImage),
                contentDescription = lesson.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.4f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(modifier = Modifier.heightIn(min = 56.dp)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = lesson.shortDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        }
    }
}
