package com.subreax.schedule

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
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


class MainActivity : AppCompatActivity() {
    private val bookmarkRepo: BookmarkRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val startDestination = runBlocking {
            if (bookmarkRepo.isNotEmpty()) {
                NavGraph.main
            } else {
                NavGraph.init
            }
        }

        setContent {
            ScheduleTheme {
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
