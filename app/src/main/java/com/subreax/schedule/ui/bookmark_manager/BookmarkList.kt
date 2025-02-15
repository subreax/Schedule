package com.subreax.schedule.ui.bookmark_manager

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.subreax.schedule.R
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.ui.theme.ScheduleTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun BookmarkList(
    bookmarks: List<ScheduleBookmark>,
    onEditClicked: (ScheduleBookmark) -> Unit,
    onRemoveClicked: (ScheduleBookmark) -> Unit,
    onMove: (from: Int, to: Int) -> Unit,
    onDragChanged: (isDragging: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentBottomPadding: Dp = 0.dp
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onMove(from.index, to.index)
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = PaddingValues(bottom = contentBottomPadding)
    ) {
        itemsIndexed(
            items = bookmarks,
            key = { _, item -> item.scheduleId.value }
        ) { _, item ->
            ReorderableItem(
                state = reorderableLazyColumnState,
                key = item.scheduleId.value
            ) {
                BookmarkItem(
                    scheduleId = item.scheduleId.value,
                    name = item.name,
                    leadingIcons = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.draggableHandle(
                                onDragStarted = {
                                    onDragChanged(true)
                                },
                                onDragStopped = {
                                    onDragChanged(false)
                                }
                            ),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.outline)
                        ) {
                            Icon(Icons.Rounded.DragHandle, "Reorder")
                        }
                    },
                    trailingIcons = {
                        BookmarkDropdownMenuWithIcon(
                            onRename = { onEditClicked(item) },
                            onDelete = { onRemoveClicked(item) }
                        )
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun BookmarkDropdownMenuWithIcon(
    onRename: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Filled.MoreVert, stringResource(R.string.options))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.rename))
                },
                leadingIcon = {
                    Icon(Icons.Filled.Edit, stringResource(R.string.rename))
                },
                onClick = {
                    expanded = false
                    onRename()
                }
            )
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.delete))
                },
                leadingIcon = {
                    Icon(Icons.Filled.Delete, stringResource(R.string.delete))
                },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookmarkListPreview() {
    val bookmarks = listOf(
        ScheduleBookmark(ScheduleId("220431", ScheduleType.Student)),
        ScheduleBookmark(ScheduleId("620221", ScheduleType.Student), "Автоматизация+1")
    )
    ScheduleTheme {
        Surface {
            BookmarkList(
                bookmarks = bookmarks,
                onEditClicked = { },
                onRemoveClicked = { },
                onMove = { _, _ -> },
                onDragChanged = { },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}