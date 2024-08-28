package com.subreax.schedule.ui.schedule_explorer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.res.stringResource
import com.subreax.schedule.R
import com.subreax.schedule.ui.UiLoadingState
import com.subreax.schedule.ui.component.TextFieldDialog
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
    val context = context()
    val snackbarHostState = _rememberSnackbarHostState()
    val coroutineScope = rememberCoroutineScope()
    val detailsSheet = rememberModalBottomSheetState()

    val schedule by viewModel.uiSchedule.collectAsState()
    val loadingState by viewModel.uiLoadingState.collectAsState()
    val pickedSubject by viewModel.pickedSubject.collectAsState()

    val isBookmarked by viewModel.isBookmarked.collectAsState()
    val isCreateBookmarkDialogShown by viewModel.isCreateBookmarkDialogShown.collectAsState()
    val bookmarkName by viewModel.bookmarkName.collectAsState()

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
            isBookmarked = isBookmarked,
            onSubjectClicked = { item ->
                viewModel.openSubjectDetails(item.id)
            },
            onBookmarkToggled = {
                if (it) {
                    viewModel.showCreateBookmarkDialog()
                } else {
                    viewModel.removeBookmark()
                    coroutineScope.launch {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = context.getString(R.string.bookmark_has_removed),
                            actionLabel = context.getString(R.string.undo),
                            duration = SnackbarDuration.Short
                        )
                        if (snackbarResult == SnackbarResult.ActionPerformed) {
                            viewModel.addBookmark()
                        }
                    }
                }
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

        if (isCreateBookmarkDialogShown) {
            TextFieldDialog(
                dialogTitle = stringResource(R.string.new_bookmark),
                value = bookmarkName,
                onValueChange = viewModel::updateBookmarkName,
                onSave = {
                    viewModel.addBookmark()
                    viewModel.hideCreateBookmarkDialog()
                },
                onDismiss = {
                    viewModel.hideCreateBookmarkDialog()
                },
                label = stringResource(R.string.bookmark_name_optional)
            )
        }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            val message = viewModel.messages.receive()
            snackbarHostState.showSnackbar(
                message = message.toString(context),
                duration = SnackbarDuration.Short
            )
        }
    }
}

@Composable
fun ScheduleExplorerScreen(
    scheduleId: String,
    loadingState: UiLoadingState,
    items: List<ScheduleItem>,
    todayItemIndex: Int,
    isBookmarked: Boolean,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
    navBack: () -> Unit,
    onBookmarkToggled: (Boolean) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        TopAppBarWithSubtitle(
            title = {
                Text(text = scheduleId)
            },
            subtitle = {
                Text(text = stringResource(R.string.schedule_viewer))
            },
            navigationIcon = {
                IconButton(onClick = navBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.go_back))
                }
            },
            actions = {
                IconButton(onClick = { onBookmarkToggled(!isBookmarked) }) {
                    if (isBookmarked) {
                        Icon(Icons.Filled.Bookmark, stringResource(R.string.add_bookmark))
                    } else {
                        Icon(Icons.Filled.BookmarkBorder, stringResource(R.string.remove_bookmark))
                    }
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
