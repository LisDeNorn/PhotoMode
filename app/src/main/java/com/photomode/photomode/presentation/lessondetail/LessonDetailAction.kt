package com.photomode.photomode.presentation.lessondetail

sealed class LessonDetailAction {
    object RefreshData : LessonDetailAction()
    object NextStep : LessonDetailAction()
    object PrevStep : LessonDetailAction()
    object CompleteLesson : LessonDetailAction()
    object ExitLesson : LessonDetailAction()
    object StartPractice : LessonDetailAction()
}


