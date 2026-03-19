package com.photomode.domain.usecase.locale

import com.photomode.domain.model.AppLocale
import com.photomode.domain.repository.AppLocaleRepository

class GetAppLocaleUseCase(
    private val repository: AppLocaleRepository
) {
    suspend operator fun invoke(): AppLocale = repository.get()
}
