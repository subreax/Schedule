package com.subreax.schedule.ui.home.drawer

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.ui.theme.ScheduleTheme

private val DrawerItemLetterSpacing = 1.sp

private val DrawerItemModifier = Modifier
    .padding(12.dp)
    .fillMaxWidth()

@Composable
fun HomeDrawerContent(
    selectedBookmark: ScheduleBookmark,
    bookmarks: List<ScheduleBookmark>,
    onBookmarkClicked: (ScheduleBookmark) -> Unit,
    navToBookmarkManager: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        HomeDrawerHeader(
            Modifier
                .height(200.dp)
                .fillMaxWidth()
        )

        DrawerSectionTitle(
            text = "Закладки",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
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

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        DrawerItem(
            onClick = navToBookmarkManager,
            leadingIcon = {
                Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Редактор закладок")
            },
            modifier = DrawerItemModifier
        ) {
            Text(
                text = "Редактор закладок",
                letterSpacing = DrawerItemLetterSpacing,
            )
        }

        DrawerItem(
            onClick = { /* TODO */ },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = "Поиск расписания")
            },
            modifier = DrawerItemModifier
        ) {
            Text(
                text = "Поиск расписания",
                letterSpacing = DrawerItemLetterSpacing,
            )
        }

        DrawerItem(
            onClick = { /* TODO */ },
            leadingIcon = {
                Icon(Icons.Outlined.Info, contentDescription = "О приложении")
            },
            modifier = DrawerItemModifier
        ) {
            Text(
                text = "О приложении",
                letterSpacing = DrawerItemLetterSpacing
            )
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
        Surface(tonalElevation = 1.dp) {
            HomeDrawerContent(
                selectedBookmark = bookmarks.first(),
                bookmarks = bookmarks,
                onBookmarkClicked = {},
                navToBookmarkManager = {},
                modifier = Modifier.widthIn(max = 320.dp).fillMaxHeight(),
            )
        }
    }
}
