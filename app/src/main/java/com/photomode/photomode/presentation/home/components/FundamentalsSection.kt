package com.photomode.photomode.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.photomode.domain.usecase.home.LessonWithStatus

@Composable
fun FundamentalsSection(
    lessons: List<LessonWithStatus>,
    cardWidth: Dp,
    onLessonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(lessons) { lessonWithStatus ->
            HorizontalLessonCard(
                lessonWithStatus = lessonWithStatus,
                onClick = { onLessonClick(lessonWithStatus.lesson.id) },
                cardWidth = cardWidth
            )
        }
    }
}

