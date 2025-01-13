package com.subreax.schedule.ui.component.schedule.item.title

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun TitleItem(
    title: String,
    state: ScheduleItem.State,
    modifier: Modifier = Modifier
) {
    val textColor = when (state) {
        ScheduleItem.State.Active -> MaterialTheme.colorScheme.primary
        ScheduleItem.State.Pending -> MaterialTheme.colorScheme.onSurfaceVariant
        ScheduleItem.State.Expired -> MaterialTheme.colorScheme.outline
    }

    Column(modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = textColor
        )

        Spacer(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(width = 64.dp, height = 1.dp)
                .background(textColor.copy(alpha = 0.5f))
        )
    }
}

@Preview(name = "Light pending")
@Composable
private fun TitleItemPendingPreview() {
    ScheduleTheme {
        Surface {
            TitleItem(
                title = "Понедельник, 01.02",
                state = ScheduleItem.State.Pending,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Light active")
@Composable
private fun TitleItemActivePreview() {
    ScheduleTheme {
        Surface {
            TitleItem(
                title = "Понедельник, 01.02",
                state = ScheduleItem.State.Active,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Light expired")
@Composable
private fun TitleItemExpiredPreview() {
    ScheduleTheme {
        Surface {
            TitleItem(
                title = "Понедельник, 01.02",
                state = ScheduleItem.State.Expired,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Dark pending", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TitleItemDarkPendingPreview() {
    ScheduleTheme {
        Surface {
            TitleItem(
                title = "Понедельник, 01.02",
                state = ScheduleItem.State.Pending,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Dark active", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TitleItemDarkActivePreview() {
    ScheduleTheme {
        Surface {
            TitleItem(
                title = "Понедельник, 01.02",
                state = ScheduleItem.State.Active,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Dark expired", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TitleItemDarkExpiredPreview() {
    ScheduleTheme {
        Surface {
            TitleItem(
                title = "Понедельник, 01.02",
                state = ScheduleItem.State.Expired,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            )
        }
    }
}
