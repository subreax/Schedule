package com.subreax.schedule.ui.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.ui.component.Subject
import com.subreax.schedule.ui.component.Title
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSubjectClicked: (HomeViewModel.ScheduleItem.Subject) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        HomeScreen(
            scheduleOwners = homeViewModel.scheduleOwners,
            currentScheduleOwner = homeViewModel.currentScheduleOwner,
            onScheduleOwnerClicked = {
                homeViewModel.loadSchedule(it)
            },
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

@Composable
private fun context(): Context {
    LocalConfiguration.current
    return LocalContext.current
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    scheduleOwners: List<ScheduleOwner>,
    currentScheduleOwner: ScheduleOwner,
    onScheduleOwnerClicked: (ScheduleOwner) -> Unit,
    schedule: List<HomeViewModel.ScheduleItem>,
    onSubjectClicked: (HomeViewModel.ScheduleItem.Subject) -> Unit,
    modifier: Modifier = Modifier
) {
    val drawer = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                HomeDrawerContent(
                    currentScheduleOwner = currentScheduleOwner,
                    scheduleOwners = scheduleOwners,
                    onScheduleOwnerClicked = {
                        coroutineScope.launch { drawer.close() }
                        onScheduleOwnerClicked(it)
                    }
                )
            }
        },
        drawerState = drawer,
        modifier = modifier
    ) {
        Column {
            HomeTopAppBar(
                subtitle = currentScheduleOwner.id,
                onMenuClicked = {
                    coroutineScope.launch {
                        drawer.open()
                    }
                }
            )

            HomeScreenContent(
                schedule = schedule,
                onSubjectClicked = onSubjectClicked
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(subtitle: String, onMenuClicked: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Расписание",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        ),
        navigationIcon = {
            IconButton(onClick = onMenuClicked) {
                Icon(Icons.Filled.Menu, contentDescription = "menu")
            }
        }
    )
}

@Composable
private fun HomeDrawerContent(
    currentScheduleOwner: ScheduleOwner,
    scheduleOwners: List<ScheduleOwner>,
    onScheduleOwnerClicked: (ScheduleOwner) -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .aspectRatio(16f / 9f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "Расписание ТулГУ",
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        scheduleOwners.forEach {
            DrawerItem(
                title = it.id,
                checked = currentScheduleOwner.id == it.id,
                onClick = { onScheduleOwnerClicked(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DrawerItem(
    title: String,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val checkedIndicatorColor = if (!checked) {
        Color.Transparent
    } else {
        MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(modifier)
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(checkedIndicatorColor)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

private val subjectModifier = Modifier
    .padding(horizontal = 16.dp, vertical = 8.dp)
    .fillMaxWidth()

private val titleModifier = Modifier.padding(
    start = 16.dp,
    end = 16.dp,
    top = 32.dp,
    bottom = 8.dp
)

@Composable
fun HomeScreenContent(
    schedule: List<HomeViewModel.ScheduleItem>,
    onSubjectClicked: (HomeViewModel.ScheduleItem.Subject) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        schedule.forEach {
            when (it) {
                is HomeViewModel.ScheduleItem.Subject -> {
                    // todo: use id as a key
                    item {
                        Subject(
                            index = it.index,
                            name = it.name,
                            infoItem1 = it.place,
                            infoItem2 = it.teacherName,
                            type = it.type,
                            onSubjectClicked = { onSubjectClicked(it) },
                            modifier = subjectModifier
                        )
                    }
                }

                is HomeViewModel.ScheduleItem.Title -> {
                    // todo: is it required to use key in this place?
                    item {
                        Title(
                            title = it.title,
                            modifier = titleModifier
                        )
                    }
                }
            }
        }
    }
}
