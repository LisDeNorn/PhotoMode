package com.photomode.photomode

import android.app.Application
import com.photomode.photomode.di.repositoryModule
import com.photomode.photomode.di.useCaseModule
import com.photomode.photomode.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Application class for Koin initialization.
 * Modules are split by layer: repositories, use cases, view models.
 */
class PhotoModeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PhotoModeApplication)
            modules(repositoryModule, useCaseModule, viewModelModule)
        }
    }
}







