package com.photomode.photomode.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.photomode.domain.model.AppLocale

/**
 * Applies [AppLocale] to the process using AppCompat per-app locales (API 24+ via AndroidX).
 */
object AppLocaleApplier {

    fun apply(locale: AppLocale) {
        val tags = when (locale) {
            AppLocale.ENGLISH -> "en"
            AppLocale.RUSSIAN -> "ru"
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tags))
    }
}
