package com.photomode.domain.usecase.locale

import com.photomode.domain.model.AppLocale
import com.photomode.domain.repository.AppLocaleRepository
import kotlinx.coroutines.flow.Flow

class ObserveAppLocaleUseCase(
    private val repository: AppLocaleRepository
) {
    operator fun invoke(): Flow<AppLocale> = repository.observe()
}
