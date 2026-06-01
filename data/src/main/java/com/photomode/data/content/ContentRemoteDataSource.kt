package com.photomode.data.content

import com.photomode.domain.model.AppLocale

interface ContentRemoteDataSource {
    suspend fun getLessonsJson(locale: AppLocale): String
    suspend fun getMissionsJson(locale: AppLocale): String
}