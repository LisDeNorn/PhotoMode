package com.photomode.photomode.di

import androidx.room.Room
import com.photomode.data.local.LessonOfTheDayCacheImpl
import com.photomode.data.local.db.PhotoModeDatabase
import com.photomode.data.repositoryImpl.LessonRepositoryImpl
import com.photomode.data.util.TimeSourceImpl
import com.photomode.data.repositoryImpl.MissionRepositoryImpl
import com.photomode.data.repositoryImpl.ProgressRepositoryImpl
import com.photomode.domain.repository.LessonOfTheDayCache
import com.photomode.domain.repository.LessonRepository
import com.photomode.domain.repository.MissionRepository
import com.photomode.domain.repository.ProgressRepository
import com.photomode.domain.util.TimeSource
import com.photomode.domain.usecase.home.GetHomeDataUseCase
import com.photomode.domain.usecase.home.SortLessonsByPriorityUseCase
import com.photomode.domain.usecase.lesson.GetFundamentalsLessonsUseCase
import com.photomode.domain.usecase.lesson.GetLessonByIdUseCase
import com.photomode.domain.usecase.lesson.GetLessonOfTheDayUseCase
import com.photomode.domain.usecase.lesson.GetLessonsByCategoryUseCase
import com.photomode.domain.usecase.lesson.GetScenariosLessonsUseCase
import com.photomode.domain.usecase.mission.GetCurrentMissionUseCase
import com.photomode.domain.usecase.progress.GetUserProgressFlowUseCase
import com.photomode.domain.usecase.progress.GetUserProgressUseCase
import com.photomode.domain.usecase.progress.MarkLessonCompletedUseCase
import com.photomode.photomode.presentation.home.HomeViewModel
import com.photomode.photomode.presentation.lessondetail.LessonDetailViewModel
import com.photomode.photomode.presentation.lessonslist.LessonsListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Repositories (singleton). */
val repositoryModule = module {
    single<LessonRepository> { LessonRepositoryImpl(get()) }
    single<ProgressRepository> { ProgressRepositoryImpl(get()) }
    single<MissionRepository> { MissionRepositoryImpl() }
    single {
        Room.databaseBuilder(
            get(),
            PhotoModeDatabase::class.java,
            "photomode_db"
        ).build()
    }
    single {
        get<PhotoModeDatabase>().completedLessonDao()
    }
    single<LessonOfTheDayCache> { LessonOfTheDayCacheImpl(get()) }
    single<TimeSource> { TimeSourceImpl() }
}

/** Use cases (factory). */
val useCaseModule = module {
    factory { GetLessonOfTheDayUseCase(get(), get()) }
    factory { GetFundamentalsLessonsUseCase(get()) }
    factory { GetScenariosLessonsUseCase(get()) }
    factory { GetLessonsByCategoryUseCase(get()) }
    factory { GetLessonByIdUseCase(get()) }
    factory { GetUserProgressUseCase(get()) }
    factory { GetUserProgressFlowUseCase(get()) }
    factory { MarkLessonCompletedUseCase(get()) }
    factory { GetCurrentMissionUseCase(get()) }
    factory { SortLessonsByPriorityUseCase() }
    factory {
        GetHomeDataUseCase(
            getLessonOfTheDayUseCase = get(),
            getFundamentalsLessonsUseCase = get(),
            getScenariosLessonsUseCase = get(),
            getCurrentMissionUseCase = get(),
            sortLessonsByPriorityUseCase = get(),
            getUserProgressFlowUseCase = get()
        )
    }
}

/** ViewModels (scoped to lifecycle). */
val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { (category: com.photomode.domain.model.LessonCategory) ->
        LessonsListViewModel(get(), category)
    }
    viewModel { (lessonId: String) ->
        LessonDetailViewModel(
            lessonId = lessonId,
            getLessonByIdUseCase = get(),
            getUserProgressUseCase = get(),
            markLessonCompletedUseCase = get()
        )
    }
}
