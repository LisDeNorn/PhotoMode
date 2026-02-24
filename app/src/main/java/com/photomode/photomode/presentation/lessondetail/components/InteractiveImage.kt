package com.photomode.photomode.presentation.lessondetail.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun InteractiveImage(
    imageUri: String,
    contentDescription: String?,
    label: String? = null,
    isExpanded: Boolean = false,
    onTap: () -> Unit = {},
    borderColor: Color = MaterialTheme.colorScheme.primary,
    labelColor: Color = Color.White,
    modifier: Modifier = Modifier,
    height: Dp = 200.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    contentScale: ContentScale = ContentScale.Crop
) {
    val isInteractive = label != null

    val labelAlpha by animateFloatAsState(
        targetValue = if (isInteractive && isExpanded) 1f else 0f,
        animationSpec = tween(300),
        label = "label_alpha"
    )

    val clickableModifier =
        if (isInteractive) {
            Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onTap
            )
        } else Modifier

    val borderModifier =
        if (isInteractive && isExpanded) {
            Modifier.border(3.dp, borderColor, shape)
        } else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .then(borderModifier)
            .then(clickableModifier)
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale
        )

        if (isInteractive && labelAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.6f * labelAlpha))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = labelColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}