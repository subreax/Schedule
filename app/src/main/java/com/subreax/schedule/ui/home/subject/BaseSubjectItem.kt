package com.subreax.schedule.ui.home.subject

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.component.TypeIndicator
import com.subreax.schedule.ui.theme.ScheduleTheme

private val subjectHeight = 44.dp

private val indexModifier = Modifier
    .width(30.dp)
    .padding(end = 8.dp)

private val typeIndicatorModifier = Modifier
    .padding(end = 8.dp, top = 2.dp, bottom = 2.dp)
    .width(4.dp)
    .fillMaxHeight()


@Composable
fun BaseSubjectItem(
    index: String,
    type: SubjectType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
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
            content()
        }
    }
}

@Composable
private fun Index(value: String, modifier: Modifier = Modifier) {
    Text(
        text = value,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun TimeIndex(value: String, modifier: Modifier = Modifier) {
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BaseSubjectItemIndexPreview() {
    ScheduleTheme {
        Surface {
            BaseSubjectItem(
                index = "1",
                type = SubjectType.Lecture,
                onClick = {  },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = "Content")
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BaseSubjectItemTimePreview() {
    ScheduleTheme {
        Surface {
            BaseSubjectItem(
                index = "13\n40",
                type = SubjectType.Lecture,
                onClick = {  },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = "Content")
            }
        }
    }
}
