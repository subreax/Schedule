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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.ui.UiLoadingState
import com.subreax.schedule.ui.UiSchedule
import com.subreax.schedule.ui.component.TextFieldDialog
import com.subreax.schedule.ui.component.TopAppBarWithSubtitle
import com.subreax.schedule.ui.component.scheduleitemlist.ScheduleItem
import com.subreax.schedule.ui.component.scheduleitemlist.ScheduleList
import com.subreax.schedule.ui.context
import com.subreax.schedule.ui.home.drawer.HomeDrawerContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onOwnerIdClicked: (String) -> Unit,
    navToScheduleOwnersManager: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val detailsSheet = rememberModalBottomSheetState()

    val schedule by homeViewModel.uiSchedule.collectAsState(UiSchedule())
    val uiLoadingState by homeViewModel.uiLoadingState.collectAsState()
    val isLoading = uiLoadingState is UiLoadingState.Loading
    val failedToLoad = uiLoadingState is UiLoadingState.Error

    val bookmarks by homeViewModel.bookmarks.collectAsState(emptyList())
    val selectedBookmark = homeViewModel.selectedBookmark

    val listState = remember(schedule) {
        LazyListState(firstVisibleItemIndex = schedule.todayItemIndex)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.navigationBars
    ) { padding ->
        HomeScreen(
            isLoading = isLoading,
            failedToLoad = failedToLoad,
            scheduleIds = bookmarks,
            selectedBookmark = selectedBookmark,
            onBookmarkSelected = { bookmark ->
                homeViewModel.getSchedule(bookmark.scheduleId.value)
            },
            navToBookmarkManager = navToScheduleOwnersManager,
            items = schedule.items,
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

    homeViewModel.pickedSubject?.let {
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

                onOwnerIdClicked(id)
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
    isLoading: Boolean,
    failedToLoad: Boolean,
    scheduleIds: List<ScheduleBookmark>,
    selectedBookmark: ScheduleBookmark,
    onBookmarkSelected: (ScheduleBookmark) -> Unit,
    navToBookmarkManager: () -> Unit,
    items: List<ScheduleItem>,
    todayItemIndex: Int,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val drawer = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerContent = {
            HomeDrawerContent(
                selectedBookmark = selectedBookmark,
                bookmarks = scheduleIds,
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
        },
        drawerState = drawer,
        modifier = modifier
    ) {
        Column {
            TopAppBarWithSubtitle(
                title = { Text("Расписание") },
                subtitle = { Text(selectedBookmark.getNameOrScheduleId()) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch { drawer.open() }
                        }
                    ) {
                        Icon(Icons.Filled.Menu, "nav back")
                    }
                }
            )

            ScheduleList(
                items = items,
                todayItemIndex = todayItemIndex,
                isLoading = isLoading,
                failedToLoad = failedToLoad,
                onSubjectClicked = onSubjectClicked,
                modifier = Modifier.fillMaxSize(),
                listState = listState
            )
        }
    }
}

private fun ScheduleBookmark.getNameOrScheduleId(): String {
    return if (hasName()) name else scheduleId.value
}
