package com.subreax.schedule.ui.bookmark_manager

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.R
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.ui.component.TextFieldDialog
import com.subreax.schedule.ui.theme.ScheduleTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookmarkManagerScreen(
    viewModel: BookmarkManagerViewModel = koinViewModel(),
    navToSchedulePicker: () -> Unit,
    navBack: () -> Unit
) {
    val bookmarks by viewModel.bookmarks.collectAsState(emptyList())

    val snackbarHostState = remember { SnackbarHostState() }

    BookmarkManagerScreen(
        bookmarks = bookmarks,
        onAddClicked = navToSchedulePicker,
        onEditClicked = viewModel::showBookmarkRenameDialog,
        onRemoveClicked = viewModel::deleteBookmark,
        onMove = viewModel::moveBookmark,
        onDragChanged = viewModel::onDragChanged,
        navBack = navBack,
        snackbarHostState = snackbarHostState
    )

    val bookmarkToRename by viewModel.bookmarkToRename.collectAsState()
    val newBookmarkName by viewModel.newBookmarkName.collectAsState()
    if (bookmarkToRename != null) {
        TextFieldDialog(
            dialogTitle = stringResource(R.string.rename_bookmark),
            value = newBookmarkName,
            onValueChange = viewModel::dialogBookmarkNameChanged,
            onSave = viewModel::updateBookmarkName,
            onDismiss = viewModel::dismissRenameBookmarkDialog,
            label = stringResource(R.string.name)
        )
    }

    LaunchedEffect(Unit) {
        var snackbarJob: Job = Job()
        viewModel.deletedBookmark.collect {
            snackbarJob.cancel()
            snackbarJob = launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Закладка удалена: ${it.nameOrId()}",
                    actionLabel = "Отмена",
                    duration = SnackbarDuration.Short
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        viewModel.addBookmark(it)
                    }

                    else -> {}
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkManagerScreen(
    bookmarks: List<ScheduleBookmark>,
    onAddClicked: () -> Unit,
    onEditClicked: (ScheduleBookmark) -> Unit,
    onRemoveClicked: (ScheduleBookmark) -> Unit,
    onMove: (Int, Int) -> Unit,
    onDragChanged: (isDragging: Boolean) -> Unit,
    navBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    ScaffoldWithSnackbarBelowFab(
        topAppBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.bookmark_editor),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                ),
                navigationIcon = {
                    IconButton(onClick = navBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.go_back))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        fab = {
            FloatingActionButton(
                onClick = onAddClicked,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, stringResource(R.string.add_bookmark))
            }
        },
        snackbarHostState = snackbarHostState
    ) {
        BookmarkList(
            bookmarks = bookmarks,
            onEditClicked = onEditClicked,
            onRemoveClicked = onRemoveClicked,
            onMove = onMove,
            onDragChanged = onDragChanged,
            modifier = Modifier.fillMaxSize(),
            contentBottomPadding = 72.dp
        )
    }
}

@Composable
private fun ScaffoldWithSnackbarBelowFab(
    topAppBar: @Composable () -> Unit,
    fab: @Composable () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.padding(WindowInsets.navigationBars.asPaddingValues())) {
        topAppBar()

        Box {
            content()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Column(
                    Modifier
                        .align(Alignment.End)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    fab()
                }

                AnimatedVisibility(visible = snackbarHostState.currentSnackbarData != null) {
                    SnackbarHost(hostState = snackbarHostState, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookmarkManagerScreenPreview() {
    ScheduleTheme {
        Surface {
            BookmarkManagerScreen(
                bookmarks = listOf(
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student)),
                    ScheduleBookmark(
                        ScheduleId("620221", ScheduleType.Student),
                        "Автоматизация+1"
                    ),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student)),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student)),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student)),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student)),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student)),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student)),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student)),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student)),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student)),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student)),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student)),
                ),
                onAddClicked = { },
                onEditClicked = { },
                onRemoveClicked = { },
                onMove = { _, _ -> },
                onDragChanged = {},
                navBack = { },
                snackbarHostState = remember { SnackbarHostState() }
            )
        }
    }
}
