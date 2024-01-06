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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.theme.ScheduleTheme
import com.subreax.schedule.utils.join

private val subjectHeight = 44.dp

private val indexModifier = Modifier
    .width(30.dp)
    .padding(end = 8.dp)

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
            .height(subjectHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (index.length < 2) {
            Index(value = index, modifier = indexModifier)
        } else {
            TimeIndex(value = index, modifier = indexModifier)
        }

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

@Composable
fun Index(value: String, modifier: Modifier = Modifier) {
    Text(
        text = value,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun TimeIndex(value: String, modifier: Modifier = Modifier) {
    Text(
        text = value,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
        lineHeight = 16.sp
    )
}

@Preview(widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SubjectPreview() {
    ScheduleTheme {
        Surface {
            Subject(
                index = "1",
                name = "Математический анал",
                infoItem1 = "Гл.-431",
                infoItem2 = "Кузнецова В. А.",
                type = SubjectType.Lecture,
                onSubjectClicked = {},
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}