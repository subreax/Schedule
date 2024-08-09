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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.subreax.schedule.R
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.ui.UiLoadingState
import com.subreax.schedule.ui.component.subject_details.SubjectDetailsBottomSheet
import com.subreax.schedule.ui.component.TextFieldDialog
import com.subreax.schedule.ui.component.TopAppBarWithSubtitle
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.ui.component.schedule.Schedule
import com.subreax.schedule.ui.context
import com.subreax.schedule.ui.formatTimeRelative
import com.subreax.schedule.ui.home.drawer.HomeDrawerContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    navToScheduleExplorer: (String) -> Unit,
    navToBookmarkManager: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val detailsSheet = rememberModalBottomSheetState()

    val schedule by homeViewModel.schedule.collectAsState()
    val loadingState by homeViewModel.loadingState.collectAsState()
    val bookmarks by homeViewModel.bookmarks.collectAsState(emptyList())
    val selectedBookmark by homeViewModel.selectedBookmark.collectAsState()
    val pickedSubject by homeViewModel.pickedSubject.collectAsState()

    val listState = remember(schedule) {
        LazyListState(firstVisibleItemIndex = schedule.todayItemIndex)
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
            items = schedule.items,
            syncTime = schedule.syncTime,
            todayItemIndex = schedule.todayItemIndex,
            onSubjectClicked = { subject ->
                homeViewModel.openSubjectDetails(subject.id)
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
                coroutineScope
                    .launch { detailsSheet.hide() }
                    .invokeOnCompletion { homeViewModel.hideSubjectDetails() }

                navToScheduleExplorer(id)
            },
            onDismiss = {
                coroutineScope
                    .launch { detailsSheet.hide() }
                    .invokeOnCompletion { homeViewModel.hideSubjectDetails() }
            },
            onRenameClicked = {
                homeViewModel.startRenaming(it.subjectId)
            },
            sheetState = detailsSheet
        )
    }

    homeViewModel.renameSubjectUseCase.targetName?.let {
        TextFieldDialog(
            dialogTitle = "Переименовать предмет",
            value = homeViewModel.renameSubjectUseCase.alias,
            onValueChange = { homeViewModel.renameSubjectUseCase.updateName(it) },
            onSave = homeViewModel::finishRenaming,
            onDismiss = homeViewModel::cancelRenaming,
            label = "Имя предмета",
            placeholder = homeViewModel.renameSubjectUseCase.originalName
        )
    }

    val context = context()
    LaunchedEffect(Unit) {
        while (isActive) {
            val errorMsg = homeViewModel.errors.receive()
            snackbarHostState.showSnackbar(errorMsg.toString(context))
        }
    }
}

@Composable
fun HomeScreen(
    loadingState: UiLoadingState,
    bookmarks: List<ScheduleBookmark>,
    selectedBookmark: ScheduleBookmark,
    onBookmarkSelected: (ScheduleBookmark) -> Unit,
    navToBookmarkManager: () -> Unit,
    items: List<ScheduleItem>,
    syncTime: Date,
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
            items = items,
            syncTime = syncTime,
            todayItemIndex = todayItemIndex,
            onSubjectClicked = onSubjectClicked,
            listState = listState,
            coroutineScope = coroutineScope
        )
    }
}

@Composable
fun HomeScreenContent(
    loadingState: UiLoadingState,
    selectedBookmark: ScheduleBookmark,
    openMenu: () -> Unit,
    items: List<ScheduleItem>,
    syncTime: Date,
    todayItemIndex: Int,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
    listState: LazyListState,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        TopAppBarWithSubtitle(
            title = { Text(selectedBookmark.nameOrId()) },
            subtitle = {
                val text = if (loadingState is UiLoadingState.Loading) {
                    stringResource(R.string.synchronizing)
                } else {
                    stringResource(R.string.updated_s, formatTimeRelative(syncTime))
                }
                Text(text)
            },
            navigationIcon = {
                IconButton(onClick = openMenu) {
                    Icon(Icons.Filled.Menu, "Открыть меню")
                }
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