package com.photomode.domain.repository

import com.photomode.domain.model.AppLocale

interface ContentSyncRepository {
    suspend fun syncContent(locale: AppLocale): ContentSyncResult
}

sealed class ContentSyncResult {
    data object Updated : ContentSyncResult()

    data class Failed(
        val message: String
    ) : ContentSyncResult()
}