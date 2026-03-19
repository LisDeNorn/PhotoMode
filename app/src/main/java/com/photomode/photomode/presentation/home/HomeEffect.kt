package com.photomode.photomode.presentation.home

import com.photomode.domain.model.AppLocale

/** One-shot events from [HomeViewModel] (apply per-app locale after persistence). */
sealed interface HomeEffect {
    data class ApplyAppLocale(val locale: AppLocale) : HomeEffect
}
