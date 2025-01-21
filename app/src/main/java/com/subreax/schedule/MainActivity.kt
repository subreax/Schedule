package com.subreax.schedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.ui.theme.ScheduleTheme
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {
    private val repo: BookmarkRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val startDestination = runBlocking {
            if (repo.isNotEmpty()) {
                NavGraph.main
            } else {
                NavGraph.init
            }
        }

        setContent {
            ScheduleTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation(startDestination)
                }
            }
        }
    }
}
