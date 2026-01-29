package com.photomode.photomode.presentation.utils

import android.content.Context
import android.net.Uri
import coil.request.ImageRequest
import coil.request.ImageResult

/**
 * Утилиты для работы с изображениями из assets
 */
object ImageUtils {
    /**
     * Формирует путь к изображению в assets для Coil
     * 
     * @param imagePath путь относительно assets (например: "images/thumbnails/light_thumb.webp")
     * @return URI строка для Coil
     */
    fun getAssetImageUri(imagePath: String): String {
        // Coil поддерживает assets через file:///android_asset/
        // Важно: три слэша после file: и правильный формат
        return "file:///android_asset/$imagePath"
    }
    
    /**
     * Формирует путь к превью урока из assets
     * 
     * @param thumbnailImage имя файла превью (например: "fundamentals_light_thumb.webp")
     * @return URI строка для Coil
     */
    fun getThumbnailUri(thumbnailImage: String): String {
        // Если путь уже полный, используем его
        val path = if (thumbnailImage.startsWith("images/")) {
            thumbnailImage
        } else {
            // Иначе добавляем путь к thumbnails
            "images/thumbnails/$thumbnailImage"
        }
        return getAssetImageUri(path)
    }
    
    /**
     * Альтернативный способ: создает AssetUri для Coil
     * Используется, если стандартный file:// не работает
     */
    fun getAssetUri(context: Context, thumbnailImage: String): Uri {
        val path = if (thumbnailImage.startsWith("images/")) {
            thumbnailImage
        } else {
            "images/thumbnails/$thumbnailImage"
        }
        // Проверяем существование файла
        return try {
            context.assets.open(path).close()
            Uri.parse("file:///android_asset/$path")
        } catch (e: Exception) {
            // Если файл не найден, все равно возвращаем URI
            Uri.parse("file:///android_asset/$path")
        }
    }
}

