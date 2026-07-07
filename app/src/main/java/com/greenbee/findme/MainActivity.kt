package com.greenbee.findme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.greenbee.findme.presentation.navigation.FindMeApp
import com.greenbee.findme.presentation.splash.AnimatedSplashScreen
import com.greenbee.findme.presentation.ui.theme.FindMeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var keepSystemSplash = true
        splashScreen.setKeepOnScreenCondition { keepSystemSplash }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FindMeTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    AnimatedSplashScreen(
                        onReady = { keepSystemSplash = false },
                        onAnimationFinished = { showSplash = false },
                    )
                } else {
                    FindMeApp()
                }
            }
        }
    }
}
