package com.subreax.schedule.ui.bookmark_manager

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookmarkManagerScreen(
    viewModel: BookmarkManagerViewModel = koinViewModel(),
    navToSchedulePicker: () -> Unit,
    navBack: () -> Unit
) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    BookmarkManagerScreen(
        bookmarks = bookmarks,
        onAddClicked = navToSchedulePicker,
        onEditClicked = viewModel::showBookmarkRenameDialog,
        onRemoveClicked = viewModel::deleteBookmark,
        navBack = navBack
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkManagerScreen(
    bookmarks: List<ScheduleBookmark>,
    onAddClicked: () -> Unit,
    onEditClicked: (ScheduleBookmark) -> Unit,
    onRemoveClicked: (ScheduleBookmark) -> Unit,
    navBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.bookmark_editor), style = MaterialTheme.typography.titleMedium)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                ),
                navigationIcon = {
                    IconButton(onClick = navBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.go_back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClicked, shape = CircleShape) {
                Icon(Icons.Filled.Add, stringResource(R.string.add_bookmark))
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        BookmarkList(
            bookmarks = bookmarks,
            onEditClicked = onEditClicked,
            onRemoveClicked = onRemoveClicked,
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(bottom = 72.dp)
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookmarkManagerScreenPreview() {
    ScheduleTheme {
        Surface {
            BookmarkManagerScreen(
                bookmarks = listOf(
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student), ScheduleBookmark.NO_NAME),
                    ScheduleBookmark(ScheduleId("620221", ScheduleType.Student), "Автоматизация+1"),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student), ScheduleBookmark.NO_NAME),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student), ScheduleBookmark.NO_NAME),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student), ScheduleBookmark.NO_NAME),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student), ScheduleBookmark.NO_NAME),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student), ScheduleBookmark.NO_NAME),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student), ScheduleBookmark.NO_NAME),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student), ScheduleBookmark.NO_NAME),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student), ScheduleBookmark.NO_NAME),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student), ScheduleBookmark.NO_NAME),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student), ScheduleBookmark.NO_NAME),
                    ScheduleBookmark(ScheduleId("220431", ScheduleType.Student), ScheduleBookmark.NO_NAME),
                ),
                onAddClicked = { },
                onEditClicked = { },
                onRemoveClicked = { },
                navBack = { }
            )
        }
    }
}
