package com.photomode.photomode

import android.app.Application
import com.photomode.photomode.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Application класс для инициализации Koin
 * 
 * Koin инициализируется один раз при запуске приложения
 * и управляет всеми зависимостями через модули.
 */
class PhotoModeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            // Передаем Context для использования в модулях
            androidContext(this@PhotoModeApplication)
            // Регистрируем модули с зависимостями
            modules(appModule)
        }
    }
}







