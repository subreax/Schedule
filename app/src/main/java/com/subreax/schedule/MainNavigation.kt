package com.subreax.schedule

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.subreax.schedule.ui.home.HomeScreen

private object Screen {
    const val home = "home"
}

@Composable
fun MainNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.home) {
        composable(Screen.home) {
            HomeScreen()
        }
    }
}
