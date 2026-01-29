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
import androidx.compose.ui.unit.dp
import com.photomode.photomode.presentation.components.*
import com.photomode.photomode.presentation.home.components.LessonCard
import com.photomode.domain.model.LessonCategory

/**
 * LessonsListScreen - чистый UI компонент (View в MVVM)
 * 
 * Принципы:
 * - Не знает про ViewModel
 * - Получает только State и Actions
 * - Легко тестируется
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsListScreen(
    state: LessonsListUiState,
    onAction: (LessonsListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryTitle = when (state.category) {
        LessonCategory.FUNDAMENTALS -> "База"
        LessonCategory.SCENARIOS -> "Сценарии"
        null -> "Уроки"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = categoryTitle) },
                navigationIcon = {
                    IconButton(onClick = { onAction(LessonsListAction.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
            state.error != null -> ErrorView(
                message = state.error ?: "Ошибка",
                onRetry = { onAction(LessonsListAction.RefreshData) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
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

