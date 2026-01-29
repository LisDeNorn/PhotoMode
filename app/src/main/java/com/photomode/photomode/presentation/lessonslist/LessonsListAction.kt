package com.photomode.photomode.presentation.lessonslist

sealed class LessonsListAction {
    data class OnLessonClick(val lessonId: String) : LessonsListAction()
    object OnBackClick : LessonsListAction()
    object RefreshData : LessonsListAction()
}







