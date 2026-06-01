package com.photomode.data.content

import com.photomode.domain.model.AppLocale
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class GistContentRemoteDataSource(
    private val client: HttpClient
) : ContentRemoteDataSource {

    override suspend fun getLessonsJson(locale: AppLocale): String {
        return client.get(ContentUrls.lessonsUrl(locale)).bodyAsText()
    }

    override suspend fun getMissionsJson(locale: AppLocale): String {
        return client.get(ContentUrls.missionsUrl(locale)).bodyAsText()
    }

    private object ContentUrls {

        private const val BASE_URL =
            "https://gist.githubusercontent.com/LisDeNorn/0f305dfb2a7a8e3462e27f19734fcac2/raw"

        fun lessonsUrl(locale: AppLocale): String {
            return when (locale) {
                AppLocale.RUSSIAN -> "$BASE_URL/lessons_ru.json"
                AppLocale.ENGLISH -> "$BASE_URL/lessons_en.json"
            }
        }

        fun missionsUrl(locale: AppLocale): String {
            return when (locale) {
                AppLocale.RUSSIAN -> "$BASE_URL/missions_ru.json"
                AppLocale.ENGLISH -> "$BASE_URL/missions_en.json"
            }
        }
    }
}