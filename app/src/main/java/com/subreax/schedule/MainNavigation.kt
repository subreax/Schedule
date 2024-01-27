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
        composable(Screen.welcome) {
            WelcomeScreen(goAhead = {
                navController.navigate(Screen.enterScheduleOwner)
            })
        }

        composable(Screen.enterScheduleOwner) {
            EnterScheduleIdScreen(
                goBack = { navController.navigateUp() },
                goAhead = {
                    navController.navigate(Screen.home) {
                        popUpTo(Screen.welcome) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.home) {
            HomeScreen(
                onSubjectClicked = { owner, subject ->
                    navController.navigate("${Screen.details}/${owner.type}/${subject.id}")
                },
                navToScheduleOwnersManager = {
                    navController.navigate(Screen.scheduleOwnersManager)
                }
            )
        }

        composable(
            route = "${Screen.details}/{owner_type_name}/{subject_id}",
            arguments = listOf(
                navArgument("owner_type_name") { type = NavType.StringType },
                navArgument("subject_id") { type = NavType.LongType }
            )
        ) {
            SubjectDetailsScreen(navBack = {
                navController.navigateUp()
            })
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
