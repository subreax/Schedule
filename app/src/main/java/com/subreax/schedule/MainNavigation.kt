package com.subreax.schedule

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.subreax.schedule.ui.about.AboutScreen
import com.subreax.schedule.ui.ac_schedule.AcademicScheduleScreen
import com.subreax.schedule.ui.bookmark_manager.BookmarkManagerScreen
import com.subreax.schedule.ui.bookmark_manager.add_bookmark.AddBookmarkScreen
import com.subreax.schedule.ui.home.HomeScreen
import com.subreax.schedule.ui.schedule_explorer.ScheduleExplorerScreen
import com.subreax.schedule.ui.search_schedule.SearchScheduleScreen
import com.subreax.schedule.ui.welcome.EnterScheduleIdScreen
import com.subreax.schedule.ui.welcome.WelcomeScreen
import com.subreax.schedule.utils.urlEncode


private const val TRANSITION_DURATION_MS = 250

object Screen {
    const val welcome = "welcome"
    const val enterScheduleId = "enter_schedule_id"
    const val home = "home"
    const val bookmarkManager = "bookmark_manager"
    const val addBookmark = "add_bookmark"
    const val searchSchedule = "search_schedule"
    const val scheduleExplorer = "schedule_explorer"
    const val about = "about"
    const val academicSchedule = "ac_schedule"
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
                    navToScheduleExplorer = { id ->
                        navController.navigateToScheduleExplorer(id)
                    },
                    navToBookmarkManager = {
                        navController.navigate(Screen.bookmarkManager)
                    },
                    navToScheduleFinder = {
                        navController.navigate(Screen.searchSchedule)
                    },
                    navToAbout = {
                        navController.navigate(Screen.about)
                    },
                    navToAcademicSchedule = { id ->
                        navController.navigateToAcademicSchedule(id)
                    }
                )
            }
        }

        composableWithSlideAnim(
            route = "${Screen.scheduleExplorer}/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) {
            ScheduleExplorerScreen(
                navToScheduleExplorer = { id ->
                    navController.navigateToScheduleExplorer(id)
                },
                navToAcademicSchedule = { id ->
                    navController.navigateToAcademicSchedule(id)
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

        composable(Screen.searchSchedule) {
            SearchScheduleScreen(
                navBack = {
                    navController.navigateUp()
                },
                navToScheduleExplorer = { id ->
                    navController.navigateToScheduleExplorer(id) {
                        popUpTo(Screen.searchSchedule) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.about) {
            AboutScreen(
                navBack = {
                    navController.navigateUp()
                }
            )
        }

        composableWithSlideAnim(route = "${Screen.academicSchedule}/{id}", arguments = listOf(
            navArgument("id") { type = NavType.StringType }
        )) {
            AcademicScheduleScreen(navBack = { navController.navigateUp() })
        }
    }
}

private fun NavHostController.navigateToScheduleExplorer(
    id: String,
    builder: NavOptionsBuilder.() -> Unit = { }
) {
    navigate("${Screen.scheduleExplorer}/${id.urlEncode()}", builder = builder)
}

private fun NavHostController.navigateToAcademicSchedule(
    id: String,
    builder: NavOptionsBuilder.() -> Unit = { }
) {
    navigate("${Screen.academicSchedule}/${id.urlEncode()}", builder = builder)
}

private fun NavGraphBuilder.composableWithSlideAnim(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = {
            slideIn(
                animationSpec = tween(TRANSITION_DURATION_MS),
                initialOffset = { IntOffset(it.width, 0) }
            ) + fadeIn(tween(TRANSITION_DURATION_MS))
        },
        exitTransition = {
            slideOut(
                animationSpec = tween(TRANSITION_DURATION_MS),
                targetOffset = { IntOffset(-it.width, 0) }
            )
        },
        popEnterTransition = {
            slideIn(
                animationSpec = tween(TRANSITION_DURATION_MS),
                initialOffset = { IntOffset(-it.width, 0) }
            )
        },
        popExitTransition = {
            slideOut(
                animationSpec = tween(TRANSITION_DURATION_MS),
                targetOffset = { IntOffset(it.width, 0) }
            ) + fadeOut(tween(TRANSITION_DURATION_MS))
        },
        content = content
    )
}
