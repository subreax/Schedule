package com.subreax.schedule.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.subreax.schedule.utils.join

private val indexModifier = Modifier
    .padding(start = 4.dp)
    .width(26.dp)

private val typeIndicatorModifier = Modifier
    .padding(end = 8.dp, top = 2.dp, bottom = 2.dp)
    .width(4.dp)
    .fillMaxHeight()

@Composable
fun Subject(
    index: String,
    name: String,
    infoItem1: String,
    infoItem2: String,
    type: SubjectType,
    onSubjectClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infoText = remember {
        val typeStr = if (type.ordinal > SubjectType.Lab.ordinal) type.name else ""
        " • ".join(infoItem1, typeStr, infoItem2)
    }

    Subject(
        index = index,
        title = name,
        subtitle = infoText,
        type = type,
        onSubjectClicked = onSubjectClicked,
        modifier = modifier
    )
}

@Composable
private fun Subject(
    index: String,
    title: String,
    subtitle: String,
    type: SubjectType,
    onSubjectClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onSubjectClicked)
            .then(modifier)
            .height(44.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = index,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = indexModifier
        )

        TypeIndicator(
            type = type,
            modifier = typeIndicatorModifier
        )

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
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
                index = "2",
                name = "Математический анал",
                infoItem1 = "Гл.-431",
                infoItem2 = "Кузнецова В. А.",
                type = SubjectType.Lecture,
                onSubjectClicked = {},
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}