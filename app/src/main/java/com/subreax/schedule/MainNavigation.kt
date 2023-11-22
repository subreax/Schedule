package com.subreax.schedule

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.subreax.schedule.ui.details.SubjectDetailsScreen
import com.subreax.schedule.ui.home.HomeScreen


private const val TRANSITION_DURATION_MS = 250

private object Screen {
    const val home = "home"
    const val details = "details"
}

@Composable
fun MainNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.home,
        enterTransition = { fadeIn(animationSpec = tween(TRANSITION_DURATION_MS)) },
        exitTransition = { fadeOut(animationSpec = tween(TRANSITION_DURATION_MS)) },
    ) {
        composable(Screen.home) {
            HomeScreen(
                onSubjectClicked = { subject ->
                    navController.navigate("${Screen.details}/${subject.id}")
                }
            )
        }

        composable(
            route = "${Screen.details}/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) {
            SubjectDetailsScreen(navBack = {
                navController.popBackStack()
            })
        }
    }
}
