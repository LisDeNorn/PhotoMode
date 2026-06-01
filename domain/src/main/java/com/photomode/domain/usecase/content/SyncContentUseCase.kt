package com.photomode.domain.usecase.content

import com.photomode.domain.model.AppLocale
import com.photomode.domain.repository.ContentSyncRepository

class SyncContentUseCase(
    private val contentSyncRepository: ContentSyncRepository
) {
    suspend operator fun invoke(locale: AppLocale) =
        contentSyncRepository.syncContent(locale)
}