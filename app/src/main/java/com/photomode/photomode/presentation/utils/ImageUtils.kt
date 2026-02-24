package com.photomode.photomode.presentation.utils

import android.content.Context
import android.net.Uri
import coil.request.ImageRequest
import coil.request.ImageResult

/** Utilities for loading images from assets (Coil). */
object ImageUtils {
    /** Builds asset URI for Coil (file:///android_asset/...). */
    fun getAssetImageUri(imagePath: String): String =
        "file:///android_asset/$imagePath"

    /** Builds thumbnail URI; prepends images/thumbnails/ if needed. */
    fun getThumbnailUri(thumbnailImage: String): String {
        val path = if (thumbnailImage.startsWith("images/")) {
            thumbnailImage
        } else {
            "images/thumbnails/$thumbnailImage"
        }
        return getAssetImageUri(path)
    }

    /** Alternative: builds Asset Uri with optional existence check. */
    fun getAssetUri(context: Context, thumbnailImage: String): Uri {
        val path = if (thumbnailImage.startsWith("images/")) {
            thumbnailImage
        } else {
            "images/thumbnails/$thumbnailImage"
        }
        return try {
            context.assets.open(path).close()
            Uri.parse("file:///android_asset/$path")
        } catch (e: Exception) {
            Uri.parse("file:///android_asset/$path")
        }
    }
}

