package com.subreax.schedule.ui.bookmark_manager

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun BookmarkList(
    bookmarks: List<ScheduleBookmark>,
    onEditClicked: (ScheduleBookmark) -> Unit,
    onRemoveClicked: (ScheduleBookmark) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(bookmarks) {
            BookmarkItem(
                scheduleId = it.scheduleId.value,
                name = it.name,
                onEditClicked = {
                    onEditClicked(it)
                },
                onRemoveClicked = { onRemoveClicked(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookmarkListPreview() {
    val bookmarks = listOf(
        ScheduleBookmark(ScheduleId("220431", ScheduleType.Student), ScheduleBookmark.NO_NAME),
        ScheduleBookmark(ScheduleId("620221", ScheduleType.Student), "Автоматизация+1")
    )
    ScheduleTheme {
        Surface {
            BookmarkList(
                bookmarks = bookmarks,
                onEditClicked = { },
                onRemoveClicked = { }
            )
        }
    }
}