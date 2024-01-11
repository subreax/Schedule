package com.subreax.schedule.ui.home.drawer

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.theme.ScheduleTheme

private val itemHeight = 48.dp

@Composable
fun DrawerItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    decorator: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(modifier)
            .height(itemHeight),
        contentAlignment = Alignment.CenterStart
    ) {
        decorator()

        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
            content()
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, widthDp = 320)
@Composable
fun HomeDrawerItemPreview() {
    ScheduleTheme {
        Surface {
            DrawerItem(
                onClick = {  },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = "Item")
            }
        }
    }
}