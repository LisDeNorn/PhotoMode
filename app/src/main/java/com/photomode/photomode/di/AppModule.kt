package com.photomode.photomode.di

import com.photmode.data.repositoryImpl.LessonRepositoryImpl
import com.photmode.data.repositoryImpl.MissionRepositoryImpl
import com.photmode.data.repositoryImpl.ProgressRepositoryImpl
import com.photomode.domain.repository.LessonRepository
import com.photomode.domain.repository.MissionRepository
import com.photomode.domain.repository.ProgressRepository
import com.photomode.domain.usecase.home.GetHomeDataUseCase
import com.photomode.domain.usecase.lesson.GetFundamentalsLessonsUseCase
import com.photomode.domain.usecase.lesson.GetLessonByIdUseCase
import com.photomode.domain.usecase.lesson.GetLessonOfTheDayUseCase
import com.photomode.domain.usecase.lesson.GetLessonsByCategoryUseCase
import com.photomode.domain.usecase.lesson.GetScenariosLessonsUseCase
import com.photomode.domain.usecase.mission.GetCurrentMissionUseCase
import com.photomode.domain.usecase.progress.CalculateProgressPercentageUseCase
import com.photomode.domain.usecase.progress.GetUserProgressUseCase
import com.photomode.domain.usecase.progress.MarkLessonCompletedUseCase
import com.photomode.photomode.presentation.home.HomeViewModel
import com.photomode.photomode.presentation.lessonslist.LessonsListViewModel
import com.photomode.photomode.presentation.lessondetail.LessonDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * AppModule - модуль Koin для Dependency Injection
 * 
 * Определяет все зависимости приложения:
 * - Repositories (singleton - один экземпляр на все приложение)
 * - Use Cases (factory - новый экземпляр каждый раз)
 * - ViewModels (viewModel - управляется жизненным циклом)
 */
val appModule = module {
    // ========== REPOSITORIES ==========
    // Singleton - создается один раз на все приложение
    single<LessonRepository> {
        LessonRepositoryImpl(get())  // get() автоматически получает Context
    }
    
    single<ProgressRepository> {
        ProgressRepositoryImpl(get())  // get() автоматически получает Context
    }
    
    single<MissionRepository> {
        MissionRepositoryImpl()
    }
    
    // ========== USE CASES ==========
    // Factory - создается новый экземпляр каждый раз при запросе
    factory { GetLessonOfTheDayUseCase(get()) }
    factory { GetFundamentalsLessonsUseCase(get()) }
    factory { GetScenariosLessonsUseCase(get()) }
    factory { GetLessonsByCategoryUseCase(get()) }
    factory { GetLessonByIdUseCase(get()) }
    
    // Use Cases для прогресса
    factory { GetUserProgressUseCase(get()) }
    factory { MarkLessonCompletedUseCase(get()) }
    factory { CalculateProgressPercentageUseCase() }
    
    // Use Cases для миссий
    factory { GetCurrentMissionUseCase(get()) }
    
    // Композитный Use Case для главного экрана
    factory {
        GetHomeDataUseCase(
            getLessonOfTheDayUseCase = get(),
            getFundamentalsLessonsUseCase = get(),
            getScenariosLessonsUseCase = get(),
            getUserProgressUseCase = get(),
            getCurrentMissionUseCase = get(),
            progressRepository = get()
        )
    }
    
    // ========== VIEWMODELS ==========
    // viewModel - Koin автоматически управляет жизненным циклом
    // Зависимости автоматически внедряются через get()
    viewModel { HomeViewModel(get(), get()) }
    
    // ViewModel с параметром (для LessonsListViewModel)
    viewModel { (category: com.photomode.domain.model.LessonCategory) ->
        LessonsListViewModel(get(), category)
    }
    
    // ViewModel с параметром lessonId (для LessonDetailViewModel)
    viewModel { (lessonId: String) ->
        LessonDetailViewModel(
            lessonId = lessonId,
            getLessonByIdUseCase = get(),
            getUserProgressUseCase = get(),
            markLessonCompletedUseCase = get()
        )
    }
}

