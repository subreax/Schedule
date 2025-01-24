package com.subreax.schedule.ui.bookmark_manager

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun BookmarkItem(
    scheduleId: String,
    name: String,
    modifier: Modifier = Modifier,
    trailingIcons: @Composable RowScope.() -> Unit = {},
    leadingIcons: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcons()

        Column(Modifier.weight(1f)) {
            if (name == ScheduleBookmark.NO_NAME) {
                Text(text = scheduleId)
            } else {
                Text(text = name)
                Text(
                    text = scheduleId,
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        trailingIcons()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookmarkItemPreview() {
    ScheduleTheme {
        Surface {
            BookmarkItem(
                scheduleId = "220431",
                name = ""
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Item with name")
@Composable
private fun BookmarkItemWithNamePreview() {
    ScheduleTheme {
        Surface {
            BookmarkItem(
                scheduleId = "220431",
                name = "ИСИТ"
            )
        }
    }
}