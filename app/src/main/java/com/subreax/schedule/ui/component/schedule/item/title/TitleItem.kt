package com.subreax.schedule.ui.component.schedule.item.title

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun TitleItem(
    title: String,
    highlighted: Boolean,
    modifier: Modifier = Modifier
) {
    val textColor = if (!highlighted) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.primary
    }

    val lineColor = if (!highlighted) {
        MaterialTheme.colorScheme.outline
    } else {
        MaterialTheme.colorScheme.primary
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
                .fillMaxWidth(0.2f)
                .height(1.dp)
                .background(lineColor)
        )
    }
}

@Preview(name = "Light")
@Composable
private fun TitleItemPreview() {
    ScheduleTheme {
        Surface {
            TitleItem(
                title = "Понедельник, 01.02",
                highlighted = false,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Light highlighted")
@Composable
private fun TitleItemHighlightedPreview() {
    ScheduleTheme {
        Surface {
            TitleItem(
                title = "Понедельник, 01.02",
                highlighted = true ,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TitleItemDarkPreview() {
    ScheduleTheme {
        Surface {
            TitleItem(
                title = "Понедельник, 01.02",
                highlighted = false,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Dark highlighted", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TitleItemDarkHighlightedPreview() {
    ScheduleTheme {
        Surface {
            TitleItem(
                title = "Понедельник, 01.02",
                highlighted = true ,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            )
        }
    }
}
