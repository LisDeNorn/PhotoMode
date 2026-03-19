package com.photomode.domain.usecase.locale

import com.photomode.domain.model.AppLocale
import com.photomode.domain.repository.AppLocaleRepository

class SetAppLocaleUseCase(
    private val repository: AppLocaleRepository
) {
    suspend operator fun invoke(locale: AppLocale) {
        repository.set(locale)
    }
}
