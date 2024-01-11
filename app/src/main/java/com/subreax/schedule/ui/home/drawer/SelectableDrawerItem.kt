package com.subreax.schedule.ui.home.drawer

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun SelectableDrawerItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    DrawerItem(
        onClick = onClick,
        modifier = modifier,
        decorator = {
            if (selected) {
                Spacer(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        },
        content = content
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, widthDp = 320)
@Composable
fun SelectableDrawerItemPreview() {
    ScheduleTheme {
        Surface {
            SelectableDrawerItem(selected = false, onClick = { }) {
                Text(
                    text = "Selectable item",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Preview(name = "Selected", uiMode = Configuration.UI_MODE_NIGHT_YES, widthDp = 320)
@Composable
fun SelectableDrawerItemPreview_selected() {
    ScheduleTheme {
        Surface {
            SelectableDrawerItem(selected = true, onClick = { }) {
                Text(
                    text = "Selectable item",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
