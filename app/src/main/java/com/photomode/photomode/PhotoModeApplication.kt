package com.photomode.photomode

import android.app.Application
import com.photomode.domain.usecase.locale.GetAppLocaleUseCase
import com.photomode.photomode.di.repositoryModule
import com.photomode.photomode.di.useCaseModule
import com.photomode.photomode.di.viewModelModule
import com.photomode.photomode.locale.AppLocaleApplier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

/**
 * Application class for Koin initialization.
 * Modules are split by layer: repositories, use cases, view models.
 * Applies persisted [com.photomode.domain.model.AppLocale] before any activity is shown.
 */
class PhotoModeApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PhotoModeApplication)
            modules(repositoryModule, useCaseModule, viewModelModule)
        }
        val initialLocale = runBlocking(Dispatchers.IO) {
            getKoin().get<GetAppLocaleUseCase>()()
        }
        AppLocaleApplier.apply(initialLocale)
    }
}







