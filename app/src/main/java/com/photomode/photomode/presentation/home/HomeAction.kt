package com.photomode.photomode.presentation.home

import com.photomode.domain.model.AppLocale

sealed class HomeAction {
    object RefreshData : HomeAction()
    data class OnLessonClick(val lessonId: String) : HomeAction()
    object OnFundamentalsClick : HomeAction()
    object OnScenariosClick : HomeAction()
    object OnProfileClick : HomeAction()
    data class SetAppLocale(val locale: AppLocale) : HomeAction()
}