package com.photomode.photomode.di

import androidx.room.Room
import com.photomode.data.content.ContentLocalDataSource
import com.photomode.data.content.ContentRemoteDataSource
import com.photomode.data.content.GistContentRemoteDataSource
import com.photomode.data.local.AppLocaleRepositoryImpl
import com.photomode.data.local.LessonOfTheDayCacheImpl
import com.photomode.data.local.db.PhotoModeDatabase
import com.photomode.data.repositoryImpl.ContentSyncRepositoryImpl
import com.photomode.data.repositoryImpl.LessonRepositoryImpl
import com.photomode.data.repositoryImpl.MissionRepositoryImpl
import com.photomode.data.repositoryImpl.ProgressRepositoryImpl
import com.photomode.data.storage.LocalLessonStorage
import com.photomode.data.storage.LocalMissionStorage
import com.photomode.data.util.TimeSourceImpl
import com.photomode.domain.repository.AppLocaleRepository
import com.photomode.domain.repository.ContentSyncRepository
import com.photomode.domain.repository.LessonOfTheDayCache
import com.photomode.domain.repository.LessonRepository
import com.photomode.domain.repository.MissionRepository
import com.photomode.domain.repository.ProgressRepository
import com.photomode.domain.usecase.content.SyncContentUseCase
import com.photomode.domain.usecase.home.GetHomeDataUseCase
import com.photomode.domain.usecase.home.SortLessonsByPriorityUseCase
import com.photomode.domain.usecase.lesson.GetFundamentalsLessonsUseCase
import com.photomode.domain.usecase.lesson.GetLessonByIdUseCase
import com.photomode.domain.usecase.lesson.GetLessonOfTheDayUseCase
import com.photomode.domain.usecase.lesson.GetLessonsByCategoryUseCase
import com.photomode.domain.usecase.lesson.GetScenariosLessonsUseCase
import com.photomode.domain.usecase.locale.GetAppLocaleUseCase
import com.photomode.domain.usecase.locale.ObserveAppLocaleUseCase
import com.photomode.domain.usecase.locale.SetAppLocaleUseCase
import com.photomode.domain.usecase.mission.GetCurrentMissionUseCase
import com.photomode.domain.usecase.progress.GetUserProgressFlowUseCase
import com.photomode.domain.usecase.progress.GetUserProgressUseCase
import com.photomode.domain.usecase.progress.MarkLessonCompletedUseCase
import com.photomode.domain.util.TimeSource
import com.photomode.photomode.presentation.home.HomeViewModel
import com.photomode.photomode.presentation.lessondetail.LessonDetailViewModel
import com.photomode.photomode.presentation.lessonslist.LessonsListViewModel
import com.photomode.photomode.presentation.profile.ProfileViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Repositories (singleton). */
val repositoryModule = module {

    single {
        HttpClient(OkHttp) {
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 10_000
            }
        }
    }

    single<ContentRemoteDataSource> {
        GistContentRemoteDataSource(
            client = get()
        )
    }

    single { ContentLocalDataSource(get()) }

    single { LocalLessonStorage() }
    single { LocalMissionStorage() }

    single {
        LessonRepositoryImpl(
            appLocaleRepository = get(),
            contentLocalDataSource = get(),
            storage = get()
        )
    }

    single<LessonRepository> {
        get<LessonRepositoryImpl>()
    }

    single {
        MissionRepositoryImpl(
            appLocaleRepository = get(),
            contentLocalDataSource = get(),
            storage = get()
        )
    }

    single<MissionRepository> {
        get<MissionRepositoryImpl>()
    }

    single<ContentSyncRepository> {
        ContentSyncRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            lessonStorage = get(),
            missionStorage = get(),
            cacheInvalidators = listOf(
                get<LessonRepositoryImpl>(),
                get<MissionRepositoryImpl>()
            )
        )
    }

    single<ProgressRepository> { ProgressRepositoryImpl(get()) }
    single<AppLocaleRepository> { AppLocaleRepositoryImpl(get()) }

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
    factory { GetAppLocaleUseCase(get()) }
    factory { ObserveAppLocaleUseCase(get()) }
    factory { SetAppLocaleUseCase(get()) }
    factory { SortLessonsByPriorityUseCase() }
    factory { SyncContentUseCase(contentSyncRepository = get()) }
    factory {
        GetHomeDataUseCase(
            getLessonOfTheDayUseCase = get(),
            getFundamentalsLessonsUseCase = get(),
            getScenariosLessonsUseCase = get(),
            getCurrentMissionUseCase = get(),
            sortLessonsByPriorityUseCase = get(),
            getUserProgressFlowUseCase = get(),
            observeAppLocaleUseCase = get()
        )
    }
}

/** ViewModels (scoped to lifecycle). */
val viewModelModule = module {
    viewModel { HomeViewModel(get(), get()) }
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
    viewModel {
        ProfileViewModel(
            getFundamentalsLessonsUseCase = get(),
            getScenariosLessonsUseCase = get(),
            getUserProgressUseCase = get(),
            getCurrentMissionUseCase = get(),
            getLessonByIdUseCase = get(),
            getAppLocaleUseCase = get(),
            syncContentUseCase = get()
        )
    }
}
