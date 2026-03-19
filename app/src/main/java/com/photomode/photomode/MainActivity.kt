package com.photomode.photomode

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.photomode.photomode.presentation.navigation.NavigationGraph
import com.photomode.photomode.ui.theme.PhotoModeTheme

/**
 * [AppCompatActivity] so [androidx.appcompat.app.AppCompatDelegate.setApplicationLocales]
 * reliably updates configuration for string resources.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoModeTheme {
                NavigationGraph()
            }
        }
    }
}
