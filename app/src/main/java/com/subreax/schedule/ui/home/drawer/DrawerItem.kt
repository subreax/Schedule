package com.subreax.schedule.ui.home.drawer

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun DrawerItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    decorator: @Composable () -> Unit = {},
    leadingIcon: @Composable () -> Unit = {},
    leadingIconPadding: Dp = 16.dp,
    text: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(modifier),
        contentAlignment = Alignment.CenterStart
    ) {
        decorator()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(leadingIconPadding),
            modifier = Modifier.fillMaxWidth()
        ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.outline) {
                leadingIcon()
            }

            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                text()
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, widthDp = 320)
@Composable
fun DrawerItemPreview() {
    ScheduleTheme {
        Surface {
            DrawerItem(
                onClick = { },
                modifier = Modifier.padding(16.dp),
                text = { Text("Find schedule") },
                leadingIcon = { Icon(Icons.Filled.Search, "") }
            )
        }
    }
}