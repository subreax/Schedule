package com.subreax.schedule.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.subreax.schedule.R
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.ui.UiLoadingState
import com.subreax.schedule.ui.component.TextFieldDialog
import com.subreax.schedule.ui.component.TopAppBarWithSubtitle
import com.subreax.schedule.ui.component.schedule.Schedule
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.ui.component.subject_details.SubjectDetailsBottomSheet
import com.subreax.schedule.ui.context
import com.subreax.schedule.ui.formatTimeRelative
import com.subreax.schedule.ui.home.drawer.HomeDrawerContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navToScheduleExplorer: (String) -> Unit,
    navToAcademicSchedule: (String) -> Unit,
    navToBookmarkManager: () -> Unit,
    navToScheduleFinder: () -> Unit,
    navToAbout: () -> Unit,
    homeViewModel: HomeViewModel = koinViewModel()
) {
    val snackbarHostState = _rememberSnackbarHostState()
    val coroutineScope = rememberCoroutineScope()

    val schedule by homeViewModel.schedule.collectAsState()
    val loadingState by homeViewModel.loadingState.collectAsState()
    val bookmarks by homeViewModel.bookmarks.collectAsState(emptyList())
    val selectedBookmark by homeViewModel.selectedBookmark.collectAsState()
    val pickedSubject by homeViewModel.pickedSubject.collectAsState()

    val detailsSheet = _rememberSheetState(pickedSubject?.subjectId ?: 0)

    val listState = _rememberLazyListState(
        firstVisibleItemIndex = schedule.todayItemIndex,
        syncTime = schedule.syncTime
    )

    var scheduleAgeMs by remember(schedule.syncTime) {
        mutableLongStateOf(System.currentTimeMillis() - schedule.syncTime.time)
    }

    LifecycleStartEffect(schedule.syncTime) {
        homeViewModel.refreshScheduleIfExpired()
        onStopOrDispose { }
    }

    LaunchedEffect(schedule.syncTime) {
        while (isActive) {
            delay(60000)
            scheduleAgeMs = System.currentTimeMillis() - schedule.syncTime.time
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.navigationBars
    ) { padding ->
        HomeScreen(
            loadingState = loadingState,
            bookmarks = bookmarks,
            selectedBookmark = selectedBookmark,
            onBookmarkSelected = { bookmark ->
                homeViewModel.getSchedule(bookmark.scheduleId.value)
            },
            navToBookmarkManager = navToBookmarkManager,
            navToScheduleFinder = navToScheduleFinder,
            navToAcademicSchedule = navToAcademicSchedule,
            navToAbout = navToAbout,
            items = schedule.items,
            scheduleAgeMs = scheduleAgeMs,
            todayItemIndex = schedule.todayItemIndex,
            onSubjectClicked = { subject ->
                homeViewModel.openSubjectDetails(subject.id)
            },
            refreshSchedule = {
                homeViewModel.forceRefresh()
            },
            listState = listState,
            coroutineScope = coroutineScope,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }

    pickedSubject?.let {
        SubjectDetailsBottomSheet(
            name = it.name,
            nameAlias = it.nameAlias,
            type = it.type,
            teacher = it.teacher,
            date = it.date,
            time = it.time,
            place = it.place,
            groups = it.groups,
            note = it.note,
            onIdClicked = { id ->
                homeViewModel.hideSubjectDetails()
                navToScheduleExplorer(id)
            },
            onDismiss = {
                homeViewModel.hideSubjectDetails()
            },
            onRenameClicked = {
                homeViewModel.startRenaming(it.subjectId)
            },
            sheetState = detailsSheet
        )
    }

    homeViewModel.renameSubjectUseCase.targetName?.let {
        TextFieldDialog(
            dialogTitle = stringResource(R.string.rename_subject),
            value = homeViewModel.renameSubjectUseCase.alias,
            onValueChange = { homeViewModel.renameSubjectUseCase.updateName(it) },
            onSave = homeViewModel::finishRenaming,
            onDismiss = homeViewModel::cancelRenaming,
            label = stringResource(R.string.subject_name),
            placeholder = homeViewModel.renameSubjectUseCase.originalName,
            coroutineScope = coroutineScope,
            hideKeyboardAndDelayActions = true
        )
    }

    val context = context()
    LaunchedEffect(context) {
        while (isActive) {
            val errorMsg = homeViewModel.errors.receive()
            snackbarHostState.showSnackbar(errorMsg.toString(context))
        }
    }
}

@Composable
private fun HomeScreen(
    loadingState: UiLoadingState,
    bookmarks: List<ScheduleBookmark>,
    selectedBookmark: ScheduleBookmark,
    onBookmarkSelected: (ScheduleBookmark) -> Unit,
    navToBookmarkManager: () -> Unit,
    navToScheduleFinder: () -> Unit,
    navToAcademicSchedule: (String) -> Unit,
    navToAbout: () -> Unit,
    refreshSchedule: () -> Unit,
    items: List<ScheduleItem>,
    scheduleAgeMs: Long,
    todayItemIndex: Int,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawer: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                windowInsets = WindowInsets(0.dp),
                modifier = Modifier.widthIn(max = 320.dp)
            ) {
                HomeDrawerContent(
                    selectedBookmark = selectedBookmark,
                    bookmarks = bookmarks,
                    onBookmarkClicked = {
                        coroutineScope.launch { drawer.close() }
                        onBookmarkSelected(it)
                    },
                    navToBookmarkManager = {
                        coroutineScope.launch { drawer.close() }
                        navToBookmarkManager()
                    },
                    navToScheduleFinder = {
                        coroutineScope.launch { drawer.close() }
                        navToScheduleFinder()
                    },
                    navToAbout = {
                        coroutineScope.launch { drawer.close() }
                        navToAbout()
                    },
                    modifier = Modifier.widthIn(max = 320.dp)
                )
            }
        },
        drawerState = drawer,
        modifier = modifier
    ) {
        HomeScreenContent(
            loadingState = loadingState,
            selectedBookmark = selectedBookmark,
            openMenu = {
                coroutineScope.launch { drawer.open() }
            },
            refreshSchedule = refreshSchedule,
            navToAcademicSchedule = navToAcademicSchedule,
            items = items,
            scheduleAgeMs = scheduleAgeMs,
            todayItemIndex = todayItemIndex,
            onSubjectClicked = onSubjectClicked,
            listState = listState,
            coroutineScope = coroutineScope
        )
    }
}

@Composable
private fun HomeScreenContent(
    loadingState: UiLoadingState,
    selectedBookmark: ScheduleBookmark,
    openMenu: () -> Unit,
    refreshSchedule: () -> Unit,
    navToAcademicSchedule: (String) -> Unit,
    items: List<ScheduleItem>,
    scheduleAgeMs: Long,
    todayItemIndex: Int,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
    listState: LazyListState,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        TopAppBarWithSubtitle(
            title = {
                Text(
                    selectedBookmark.nameOrId(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            subtitle = {
                val text = if (loadingState is UiLoadingState.Loading) {
                    stringResource(R.string.synchronizing)
                } else {
                    stringResource(R.string.updated_s, formatTimeRelative(scheduleAgeMs))
                }
                Text(text)
            },
            navigationIcon = {
                IconButton(onClick = openMenu) {
                    Icon(Icons.Filled.Menu, stringResource(R.string.open_drawer))
                }
            },
            actions = {
                HomeScreenActions(
                    selectedBookmark = selectedBookmark,
                    refreshSchedule = refreshSchedule,
                    navToAcademicSchedule = navToAcademicSchedule
                )
            }
        )

        Schedule(
            items = items,
            todayItemIndex = todayItemIndex,
            loadingState = loadingState,
            onSubjectClicked = onSubjectClicked,
            modifier = Modifier.fillMaxSize(),
            listState = listState,
            coroutineScope = coroutineScope
        )
    }
}

@Composable
private fun HomeScreenActions(
    selectedBookmark: ScheduleBookmark,
    refreshSchedule: () -> Unit,
    navToAcademicSchedule: (String) -> Unit
) {
    val menuState = remember { HomeDropdownMenuState() }

    IconButton(onClick = { menuState.show() }) {
        Icon(Icons.Filled.MoreVert, contentDescription = "Меню")
    }

    HomeDropdownMenu(
        state = menuState,
        scheduleType = selectedBookmark.scheduleId.type,
        refreshSchedule = refreshSchedule,
        navToAcademicSchedule = {
            navToAcademicSchedule(selectedBookmark.scheduleId.value)
        }
    )
}

@Composable
private fun _rememberSnackbarHostState(): SnackbarHostState {
    return remember { SnackbarHostState() }
}

@Composable
private fun _rememberLazyListState(firstVisibleItemIndex: Int, syncTime: Date): LazyListState {
    return rememberSaveable(
        inputs = arrayOf(syncTime.time),
        saver = LazyListState.Saver
    ) {
        LazyListState(firstVisibleItemIndex = firstVisibleItemIndex)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun _rememberSheetState(key: Long): SheetState {
    val density = LocalDensity.current
    return rememberSaveable(
        inputs = arrayOf(key),
        saver = SheetState.Saver(
            skipPartiallyExpanded = false,
            confirmValueChange = { true },
            density = density
        )
    ) {
        SheetState(false, density)
    }
}
