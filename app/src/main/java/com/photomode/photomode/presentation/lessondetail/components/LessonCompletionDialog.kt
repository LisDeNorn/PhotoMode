package com.photomode.photomode.presentation.lessondetail.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.photomode.photomode.R

@Composable
fun LessonCompletionDialog(onExit: () -> Unit) {
    AlertDialog(
        onDismissRequest = onExit,
        title = { Text(stringResource(R.string.lesson_completed_title)) },
        text = { Text(stringResource(R.string.lesson_completed_message)) },
        confirmButton = {
            Button(onClick = onExit) {
                Text(stringResource(R.string.lesson_completed_button))
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}