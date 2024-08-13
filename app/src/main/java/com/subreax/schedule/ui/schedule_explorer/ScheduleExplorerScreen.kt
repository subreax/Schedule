package com.subreax.schedule.ui.schedule_explorer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.subreax.schedule.ui.UiLoadingState
import com.subreax.schedule.ui.component.TopAppBarWithSubtitle
import com.subreax.schedule.ui.component.schedule.Schedule
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.ui.component.subject_details.SubjectDetailsBottomSheet
import com.subreax.schedule.ui.context
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleExplorerScreen(
    viewModel: ScheduleExplorerViewModel,
    navToScheduleExplorer: (String) -> Unit,
    navBack: () -> Unit,
) {
    val snackbarHostState = _rememberSnackbarHostState()
    val coroutineScope = rememberCoroutineScope()
    val detailsSheet = rememberModalBottomSheetState()

    val schedule by viewModel.uiSchedule.collectAsState()
    val loadingState by viewModel.uiLoadingState.collectAsState()
    val pickedSubject by viewModel.pickedSubject.collectAsState()

    val listState = _rememberLazyListState(schedule.todayItemIndex)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.navigationBars
    ) { paddings ->
        ScheduleExplorerScreen(
            scheduleId = viewModel.scheduleId,
            loadingState = loadingState,
            items = schedule.items,
            todayItemIndex = schedule.todayItemIndex,
            onSubjectClicked = { item ->
                viewModel.openSubjectDetails(item.id)
            },
            navBack = navBack,
            listState = listState,
            modifier = Modifier.padding(paddings)
        )

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
                        .invokeOnCompletion { viewModel.hideSubjectDetails() }

                    navToScheduleExplorer(id)
                },
                onDismiss = {
                    coroutineScope
                        .launch { detailsSheet.hide() }
                        .invokeOnCompletion { viewModel.hideSubjectDetails() }
                },
                onRenameClicked = null,
                sheetState = detailsSheet
            )
        }
    }

    val context = context()
    LaunchedEffect(Unit) {
        while (isActive) {
            val errorMsg = viewModel.errors.receive()
            snackbarHostState.showSnackbar(errorMsg.toString(context))
        }
    }
}

@Composable
fun ScheduleExplorerScreen(
    scheduleId: String,
    loadingState: UiLoadingState,
    items: List<ScheduleItem>,
    todayItemIndex: Int,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
    navBack: () -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        TopAppBarWithSubtitle(
            title = {
                Text(text = scheduleId)
            },
            subtitle = {
                Text(text = "Просмотр расписания")
            },
            navigationIcon = {
                IconButton(onClick = navBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "nav back")
                }
            }
        )

        Schedule(
            loadingState = loadingState,
            items = items,
            todayItemIndex = todayItemIndex,
            onSubjectClicked = onSubjectClicked,
            modifier = Modifier.fillMaxSize(),
            listState = listState
        )
    }
}

@Composable
private fun _rememberSnackbarHostState(): SnackbarHostState {
    return remember { SnackbarHostState() }
}

@Composable
private fun _rememberLazyListState(firstVisibleItemIndex: Int): LazyListState {
    return rememberSaveable(inputs = arrayOf(firstVisibleItemIndex), saver = LazyListState.Saver) {
        LazyListState(firstVisibleItemIndex = firstVisibleItemIndex)
    }
}
