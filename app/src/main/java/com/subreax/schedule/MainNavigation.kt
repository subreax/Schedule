package com.subreax.schedule

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.subreax.schedule.ui.bookmark_manager.BookmarkManagerScreen
import com.subreax.schedule.ui.bookmark_manager.add_bookmark.AddBookmarkScreen
import com.subreax.schedule.ui.home.HomeScreen
import com.subreax.schedule.ui.scheduleexplorer.ScheduleExplorerScreen
import com.subreax.schedule.ui.scheduleexplorer.ScheduleExplorerViewModel
import com.subreax.schedule.ui.welcome.EnterScheduleIdScreen
import com.subreax.schedule.ui.welcome.WelcomeScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


private const val TRANSITION_DURATION_MS = 250

object Screen {
    const val welcome = "welcome"
    const val enterScheduleId = "enter_schedule_id"
    const val home = "home"
    const val bookmarkManager = "bookmark_manager"
    const val addBookmark = "add_bookmark"
    const val scheduleExplorer = "schedule_explorer"
}

object NavGraph {
    const val init = "init"
    const val main = "main"
}

@Composable
fun MainNavigation(
    startDestination: String,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(TRANSITION_DURATION_MS)) },
        exitTransition = { fadeOut(animationSpec = tween(TRANSITION_DURATION_MS)) },
    ) {
        navigation(route = NavGraph.init, startDestination = Screen.welcome) {
            composable(Screen.welcome) {
                WelcomeScreen(goAhead = {
                    navController.navigate(Screen.enterScheduleId)
                })
            }

            composable(Screen.enterScheduleId) {
                EnterScheduleIdScreen(
                    goBack = { navController.navigateUp() },
                    goAhead = {
                        navController.navigate(NavGraph.main) {
                            popUpTo(NavGraph.init) { inclusive = true }
                        }
                    }
                )
            }
        }

        navigation(route = NavGraph.main, startDestination = Screen.home) {
            composable(Screen.home) {
                HomeScreen(
                    homeViewModel = hiltViewModel(),
                    navToScheduleExplorer = { id ->
                        navController.navigate("${Screen.scheduleExplorer}/$id")
                    },
                    navToBookmarkManager = {
                        navController.navigate(Screen.bookmarkManager)
                    }
                )
            }
        }

        composable(
            route = "${Screen.scheduleExplorer}/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) {
            ScheduleExplorerScreen(
                viewModel = hiltViewModel<ScheduleExplorerViewModel>(),
                navToScheduleExplorer = { id ->
                    navController.navigate("${Screen.scheduleExplorer}/${id.urlEncode()}")
                },
                navBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(route = Screen.bookmarkManager) {
            BookmarkManagerScreen(
                navToSchedulePicker = {
                    navController.navigate(Screen.addBookmark)
                },
                navBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(Screen.addBookmark) {
            AddBookmarkScreen(
                navBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}

private fun String.urlEncode(): String {
    return URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
}