package com.subreax.schedule.ui.home.drawer

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun HomeDrawerContent(
    selectedBookmark: ScheduleBookmark,
    bookmarks: List<ScheduleBookmark>,
    onBookmarkClicked: (ScheduleBookmark) -> Unit,
    navToBookmarkManager: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(windowInsets = WindowInsets(0.dp), modifier = modifier) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Text(
                    text = "Расписание ТулГУ",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomStart),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Закладки",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodyMedium
            )

            bookmarks.forEach {
                SelectableDrawerItem(
                    selected = selectedBookmark.scheduleId == it.scheduleId,
                    onClick = { onBookmarkClicked(it) },
                ) {
                    Text(
                        text = it.toPrettyString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }

            DrawerItem(
                onClick = navToBookmarkManager,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.Tune,
                        contentDescription = "Настроить закладки",
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Настроить закладки".uppercase(),
                        fontSize = 12.sp,
                        letterSpacing = 1.5.sp,
                    )
                }
            }
        }
    }
}

private fun ScheduleBookmark.toPrettyString(): String {
    return if (hasName())
        "$name (${scheduleId.value})"
    else
        scheduleId.value
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeDrawerContentPreview() {
    val bookmarks = listOf(
        ScheduleBookmark(ScheduleId("220431", ScheduleType.Student), ScheduleBookmark.NO_NAME),
        ScheduleBookmark(ScheduleId("620221", ScheduleType.Student), ScheduleBookmark.NO_NAME)
    )

    ScheduleTheme {
        HomeDrawerContent(
            selectedBookmark = bookmarks.first(),
            bookmarks = bookmarks,
            onBookmarkClicked = {},
            navToBookmarkManager = {},
            modifier = Modifier.fillMaxHeight(),
        )
    }
}
