package com.subreax.schedule.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.theme.ScheduleTheme

private val subjectTypeEmoji = arrayOf("✏️", "\uD83D\uDCAA", "\uD83D\uDC85")

@Composable
fun Subject(
    name: String,
    place: String,
    timeRange: String,
    type: SubjectType,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        TypeIndicator(type = type, modifier = Modifier.padding(end = 8.dp))

        Column {
            Text(
                text = "$timeRange • $place",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline, // replace to onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 1.dp)
            ) {
                Text(
                    text = name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = subjectTypeEmoji[type.ordinal],
                    modifier = Modifier
                        .defaultMinSize(32.dp, Dp.Unspecified)
                        .padding(start = 4.dp)
                )
            }
        }
    }
}

@Preview(widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SubjectPreview() {
    ScheduleTheme {
        Surface {
            Subject(
                name = "Математический анал",
                place = "Гл.-431",
                timeRange = "13:40 - 15:15",
                type = SubjectType.Lecture,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}