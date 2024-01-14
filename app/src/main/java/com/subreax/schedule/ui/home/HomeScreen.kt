package com.subreax.schedule.ui.home

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.ui.home.drawer.HomeDrawerContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSubjectClicked: (HomeViewModel.ScheduleItem.Subject) -> Unit,
    navToScheduleOwnersManager: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scheduleOwners by homeViewModel.scheduleOwners.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.navigationBars
    ) { padding ->
        HomeScreen(
            isLoading = homeViewModel.isLoading,
            scheduleOwners = scheduleOwners,
            currentScheduleOwner = homeViewModel.currentScheduleOwner,
            onScheduleOwnerClicked = {
                homeViewModel.loadSchedule(it)
            },
            navToScheduleOwnersManager = navToScheduleOwnersManager,
            schedule = homeViewModel.schedule,
            onSubjectClicked = onSubjectClicked,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }

    val context = context()
    LaunchedEffect(Unit) {
        homeViewModel.errors.collectLatest {
            snackbarHostState.showSnackbar(it.toString(context))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isLoading: Boolean,
    scheduleOwners: List<ScheduleOwner>,
    currentScheduleOwner: ScheduleOwner,
    onScheduleOwnerClicked: (ScheduleOwner) -> Unit,
    navToScheduleOwnersManager: () -> Unit,
    schedule: List<HomeViewModel.ScheduleItem>,
    onSubjectClicked: (HomeViewModel.ScheduleItem.Subject) -> Unit,
    modifier: Modifier = Modifier
) {
    val drawer = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            HomeDrawerContent(
                currentScheduleOwner = currentScheduleOwner,
                scheduleOwners = scheduleOwners,
                onScheduleOwnerClicked = {
                    coroutineScope.launch { drawer.close() }
                    onScheduleOwnerClicked(it)
                },
                navToScheduleOwnersManager = {
                    coroutineScope.launch { drawer.close() }
                    navToScheduleOwnersManager()
                }
            )
        },
        drawerState = drawer,
        modifier = modifier
    ) {
        Column {
            HomeTopAppBar(
                subtitle = currentScheduleOwner.getNameOrIdIfEmpty(),
                onMenuClicked = {
                    coroutineScope.launch {
                        drawer.open()
                    }
                }
            )

            HomeScreenContent(
                isLoading = isLoading,
                schedule = schedule,
                onSubjectClicked = onSubjectClicked
            )
        }
    }
}

private fun ScheduleOwner.getNameOrIdIfEmpty(): String {
    return name.ifEmpty { id }
}

@Composable
private fun context(): Context {
    LocalConfiguration.current
    return LocalContext.current
}
