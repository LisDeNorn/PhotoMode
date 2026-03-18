package com.photomode.photomode.presentation.profile

sealed class ProfileAction {
    object RefreshData : ProfileAction()
    object OnBackClick : ProfileAction()
    object OnContinueMissionClick : ProfileAction()
}
