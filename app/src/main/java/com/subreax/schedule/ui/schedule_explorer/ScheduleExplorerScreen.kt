package com.subreax.schedule.ui.schedule_explorer

import android.content.Intent
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.LifecycleStartEffect
import com.subreax.schedule.R
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.ui.UiLoadingState
import com.subreax.schedule.ui.component.TopAppBarWithSubtitle
import com.subreax.schedule.ui.component.dialog.TextInputDialog
import com.subreax.schedule.ui.component.schedule.Schedule
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.ui.component.subject_details.SubjectDetailsBottomSheet
import com.subreax.schedule.ui.component.subject_details.toUri
import com.subreax.schedule.ui.component.util.AutoFocusable
import com.subreax.schedule.ui.context
import com.subreax.schedule.utils.ObjectHolder
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleExplorerScreen(
    navToScheduleExplorer: (String) -> Unit,
    navToAcademicSchedule: (String) -> Unit,
    navBack: () -> Unit,
    viewModel: ScheduleExplorerViewModel = koinViewModel(),
) {
    val context = context()
    val snackbarHostState = _rememberSnackbarHostState()
    val coroutineScope = rememberCoroutineScope()

    val scheduleId = viewModel.scheduleId
    val schedule by viewModel.uiSchedule.collectAsState()
    val loadingState by viewModel.uiLoadingState.collectAsState()
    val pickedSubject by viewModel.pickedSubject.collectAsState()
    val detailsSheet = _rememberSheetState(pickedSubject?.subjectId ?: 0)

    val isBookmarked by viewModel.isBookmarked.collectAsState()
    val isCreateBookmarkDialogShown by viewModel.isCreateBookmarkDialogShown.collectAsState()
    val bookmarkName by viewModel.bookmarkName.collectAsState()

    val listState = _rememberLazyListState(
        firstVisibleItemIndex = schedule.todayItemIndex,
        syncTime = schedule.syncTime
    )

    val snackbarJobHolder: ObjectHolder<Job> = remember { ObjectHolder(Job()) }
    fun launchSnackbarCoroutine(action: suspend () -> Unit) {
        snackbarJobHolder.value.cancel()
        snackbarJobHolder.value = coroutineScope.launch {
            action()
        }
    }

    LifecycleStartEffect(Unit) {
        viewModel.refreshIfNeeded()
        onStopOrDispose {  }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.navigationBars
    ) { paddings ->
        ScheduleExplorerScreen(
            scheduleId = scheduleId,
            scheduleType = schedule.id.type,
            loadingState = loadingState,
            items = schedule.items,
            todayItemIndex = schedule.todayItemIndex,
            isBookmarked = isBookmarked,
            onSubjectClicked = { item ->
                viewModel.openSubjectDetails(item.id)
            },
            onCancelSync = {
                viewModel.cancelSync()
            },
            onBookmarkToggled = {
                if (it) {
                    viewModel.showCreateBookmarkDialog()
                } else {
                    viewModel.removeBookmark()
                    launchSnackbarCoroutine {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = context.getString(R.string.bookmark_removed),
                            actionLabel = context.getString(R.string.undo),
                            duration = SnackbarDuration.Short
                        )
                        if (snackbarResult == SnackbarResult.ActionPerformed) {
                            viewModel.addBookmark()
                        }
                    }
                }
            },
            navToAcademicSchedule = { navToAcademicSchedule(scheduleId) },
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
                showPlaceOnMap = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = it.place.mapPoint!!.toUri()
                    }
                    context.startActivity(intent)
                },
                sheetState = detailsSheet
            )
        }

        if (isCreateBookmarkDialogShown) {
            AutoFocusable { focusRequester ->
                TextInputDialog(
                    title = stringResource(R.string.new_bookmark),
                    value = bookmarkName,
                    onValueChange = viewModel::updateBookmarkName,
                    onConfirm = viewModel::addBookmark,
                    onDismissRequest = viewModel::hideCreateBookmarkDialog,
                    focusRequester = focusRequester,
                    label = stringResource(R.string.name),
                    placeholder = stringResource(R.string.par_optional)
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            val message = viewModel.messages.receive().toString(context)
            launchSnackbarCoroutine {
                snackbarHostState.showSnackbar(message)
            }
        }
    }
}

@Composable
fun ScheduleExplorerScreen(
    scheduleId: String,
    scheduleType: ScheduleType,
    loadingState: UiLoadingState,
    items: List<ScheduleItem>,
    todayItemIndex: Int,
    isBookmarked: Boolean,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
    navToAcademicSchedule: () -> Unit,
    onCancelSync: () -> Unit,
    navBack: () -> Unit,
    onBookmarkToggled: (Boolean) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        TopAppBarWithSubtitle(
            title = {
                Text(text = scheduleId, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                if (scheduleType == ScheduleType.Student) {
                    IconButton(onClick = navToAcademicSchedule) {
                        Icon(Icons.Filled.CalendarMonth, stringResource(R.string.academic_schedule))
                    }
                }

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
            onCancelSync = onCancelSync,
            listState = listState
        )
    }
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
            density = density,
            skipHiddenState = false
        )
    ) {
        SheetState(false, density)
    }
}