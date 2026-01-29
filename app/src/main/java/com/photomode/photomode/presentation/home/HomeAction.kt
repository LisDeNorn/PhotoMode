package com.photomode.photomode.presentation.home

sealed class HomeAction {
    object RefreshData : HomeAction()
    data class OnLessonClick(val lessonId: String) : HomeAction()
    object OnFundamentalsClick : HomeAction()
    object OnScenariosClick : HomeAction()
    object OnProfileClick : HomeAction()
}