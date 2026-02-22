package com.photomode.photomode.presentation.lessonslist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.photomode.photomode.R
import com.photomode.photomode.presentation.components.LessonCard
import com.photomode.photomode.presentation.components.ErrorView
import com.photomode.photomode.presentation.components.LoadingView
import com.photomode.domain.model.LessonCategory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsListScreen(
    state: LessonsListUiState,
    onAction: (LessonsListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryTitle = when (state.category) {
        LessonCategory.FUNDAMENTALS -> stringResource(R.string.fundamental)
        LessonCategory.SCENARIOS -> stringResource(R.string.scenarios)
        null -> stringResource(R.string.lessons)
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                title = { Text(text = categoryTitle) },
                navigationIcon = {
                    IconButton(onClick = { onAction(LessonsListAction.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> LoadingView(
                    modifier = Modifier.align(Alignment.Center)
                )
                state.error != null -> ErrorView(
                    message = state.error,
                    onRetry = { onAction(LessonsListAction.RefreshData) },
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.TopStart),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.lessons) { lesson ->
                            LessonCard(
                                lesson = lesson,
                                onClick = { onAction(LessonsListAction.OnLessonClick(lesson.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

