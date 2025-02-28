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
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Upgrade
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subreax.schedule.R
import com.subreax.schedule.data.model.AppUpdateInfo
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
    appUpdate: AppUpdateInfo?,
    onBookmarkClicked: (ScheduleBookmark) -> Unit,
    navToBookmarkManager: () -> Unit,
    navToScheduleFinder: () -> Unit,
    navToSettings: () -> Unit,
    navToAbout: () -> Unit,
    showAppUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        HomeDrawerHeader(
            Modifier
                .height(200.dp)
                .fillMaxWidth()
        )

        DrawerSectionTitle(
            text = stringResource(R.string.bookmarks),
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
                Icon(
                    Icons.Outlined.BookmarkBorder,
                    contentDescription = stringResource(R.string.bookmark_editor)
                )
            },
            modifier = DrawerItemModifier
        ) {
            Text(
                text = stringResource(R.string.bookmark_editor),
                letterSpacing = DrawerItemLetterSpacing,
            )
        }

        DrawerItem(
            onClick = navToScheduleFinder,
            leadingIcon = {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = stringResource(R.string.find_schedule)
                )
            },
            modifier = DrawerItemModifier
        ) {
            Text(
                text = stringResource(R.string.find_schedule),
                letterSpacing = DrawerItemLetterSpacing,
            )
        }

        DrawerItem(
            onClick = navToSettings,
            leadingIcon = {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            },
            modifier = DrawerItemModifier
        ) {
            Text(
                text = stringResource(R.string.settings),
                letterSpacing = DrawerItemLetterSpacing
            )
        }

        DrawerItem(
            onClick = navToAbout,
            leadingIcon = {
                Icon(Icons.Outlined.Info, contentDescription = stringResource(R.string.about))
            },
            modifier = DrawerItemModifier
        ) {
            Text(
                text = stringResource(R.string.about),
                letterSpacing = DrawerItemLetterSpacing
            )
        }

        if (appUpdate != null) {
            val text = stringResource(R.string.update_available)
            DrawerItem(
                onClick = showAppUpdate,
                leadingIcon = {
                    Icon(Icons.Outlined.Upgrade, contentDescription = text)
                },
                modifier = DrawerItemModifier
            ) {
                Text(
                    text = text,
                    letterSpacing = DrawerItemLetterSpacing
                )
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
        ScheduleBookmark(ScheduleId("220431", ScheduleType.Student)),
        ScheduleBookmark(ScheduleId("620221", ScheduleType.Student))
    )

    ScheduleTheme {
        Surface(tonalElevation = 1.dp) {
            HomeDrawerContent(
                selectedBookmark = bookmarks.first(),
                bookmarks = bookmarks,
                appUpdate = AppUpdateInfo("", "", "", 0),
                onBookmarkClicked = {},
                navToBookmarkManager = {},
                navToScheduleFinder = {},
                navToSettings = {},
                navToAbout = {},
                showAppUpdate = {},
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .fillMaxHeight(),
            )
        }
    }
}
