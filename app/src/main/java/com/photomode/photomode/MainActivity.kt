package com.photomode.photomode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.photomode.photomode.presentation.navigation.NavigationGraph
import com.photomode.photomode.ui.theme.PhotoModeTheme

class MainActivity : ComponentActivity() {
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
