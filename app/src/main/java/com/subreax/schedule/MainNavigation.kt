package com.subreax.schedule

import android.os.Bundle
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.subreax.schedule.ui.details.SubjectDetailsScreen
import com.subreax.schedule.ui.home.HomeScreen
import com.subreax.schedule.ui.home.HomeViewModel
import com.subreax.schedule.ui.scheduleexplorer.ScheduleExplorerScreen
import com.subreax.schedule.ui.scheduleexplorer.ScheduleExplorerViewModel
import com.subreax.schedule.ui.scheduleownermgr.ScheduleOwnersManagerScreen
import com.subreax.schedule.ui.scheduleownermgr.ownerpicker.ScheduleOwnerPickerScreen
import com.subreax.schedule.ui.welcome.EnterScheduleIdScreen
import com.subreax.schedule.ui.welcome.WelcomeScreen


private const val TRANSITION_DURATION_MS = 250

object Screen {
    const val welcome = "welcome"
    const val enterScheduleOwner = "enter_schedule_owner"
    const val home = "home"
    const val details = "details"
    const val scheduleOwnersManager = "schedule_owners_manager"
    const val scheduleOwnerPicker = "schedule_owner_picker"

    const val scheduleExplorerHome = "schedule_explorer_home"
    const val scheduleExplorerDetails = "schedule_explorer_home/details"
}

object NavGraph {
    const val init = "init"
    const val main = "main"
    const val explorer = "explorer"
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
                    navController.navigate(Screen.enterScheduleOwner)
                })
            }

            composable(Screen.enterScheduleOwner) {
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
                    homeViewModel = sharedViewModel(navController, it),
                    navToDetails = {
                        navController.navigate(Screen.details)
                    },
                    navToScheduleOwnersManager = {
                        navController.navigate(Screen.scheduleOwnersManager)
                    }
                )
            }

            composable(Screen.details) {
                SubjectDetailsScreen(
                    scheduleViewModel = sharedViewModel<HomeViewModel>(navController, it),
                    onIdClicked = { id ->
                        navController.navigate("${NavGraph.explorer}/$id")
                    },
                    navBack = {
                        navController.navigateUp()
                    }
                )
            }
        }

        navigation(
            route = "${NavGraph.explorer}/{ownerId}",
            startDestination = Screen.scheduleExplorerHome,
            arguments = listOf(navArgument("ownerId") { type = NavType.StringType })
        ) {
            composable(Screen.scheduleExplorerHome) {
                val ownerId = parentArguments(navController, it)?.getString("ownerId") ?: "no_owner"

                ScheduleExplorerScreen(
                    viewModel = sharedViewModel<ScheduleExplorerViewModel>(navController, it),
                    ownerId = ownerId,
                    navToDetails = {
                        navController.navigate(Screen.scheduleExplorerDetails)
                    },
                    navBack = {
                        navController.navigateUp()
                    }
                )
            }

            composable(Screen.scheduleExplorerDetails) {
                val viewModel = sharedViewModel<ScheduleExplorerViewModel>(navController, it)

                SubjectDetailsScreen(
                    scheduleViewModel = viewModel,
                    onIdClicked = { id ->
                        navController.navigate("${NavGraph.explorer}/$id")
                    },
                    navBack = {
                        navController.navigateUp()
                    }
                )
            }
        }


        composable(route = Screen.scheduleOwnersManager) {
            ScheduleOwnersManagerScreen(
                navToSchedulePicker = {
                    navController.navigate(Screen.scheduleOwnerPicker)
                },
                navBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(Screen.scheduleOwnerPicker) {
            ScheduleOwnerPickerScreen(
                navBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}

@Composable
fun parentEntry(navController: NavController, navBackStackEntry: NavBackStackEntry): NavBackStackEntry {
    val navGraphRoute = navBackStackEntry.destination.parent?.route!!
    return remember(navBackStackEntry) {
        navController.getBackStackEntry(navGraphRoute)
    }
}

@Composable
inline fun <reified T : ViewModel> sharedViewModel(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry
): T {
    val parent = parentEntry(navController, navBackStackEntry)
    return hiltViewModel(parent)
}

@Composable
fun parentArguments(navController: NavController, navBackStackEntry: NavBackStackEntry): Bundle? {
    val parent = parentEntry(navController, navBackStackEntry)
    return parent.arguments
}