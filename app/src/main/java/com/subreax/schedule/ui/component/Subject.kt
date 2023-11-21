package com.subreax.schedule.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.theme.ScheduleTheme

private val indexModifier = Modifier.padding(start = 4.dp).width(26.dp)

private val typeIndicatorModifier = Modifier
    .padding(end = 8.dp, top = 2.dp, bottom = 2.dp)
    .width(4.dp)
    .fillMaxHeight()

@Composable
fun Subject(
    index: Int,
    name: String,
    place: String,
    timeRange: String,
    type: SubjectType,
    modifier: Modifier = Modifier
) {
    val infoText = remember { "$timeRange • $place" }

    Row(modifier = modifier.height(41.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$index",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = indexModifier
        )

        TypeIndicator(
            type = type,
            modifier = typeIndicatorModifier
        )

        Column {
            Text(
                text = infoText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SubjectPreview() {
    ScheduleTheme {
        Surface {
            Subject(
                index = 2,
                name = "Математический анал",
                place = "Гл.-431",
                timeRange = "13:40 - 15:15",
                type = SubjectType.Lecture,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}