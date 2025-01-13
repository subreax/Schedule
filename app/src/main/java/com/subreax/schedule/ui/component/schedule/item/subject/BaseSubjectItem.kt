package com.subreax.schedule.ui.component.schedule.item.subject

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.component.TypeIndicator
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.ui.theme.ScheduleTheme
import com.subreax.schedule.ui.theme.subjectColorFrom


private val typeIndicatorModifier = Modifier
    .padding(vertical = 2.dp)
    .width(4.dp)
    .fillMaxHeight()


@Composable
fun BaseSubjectItem(
    index: String,
    type: SubjectType,
    onClick: () -> Unit,
    state: ScheduleItem.State,
    modifier: Modifier = Modifier,
    indexModifier: Modifier = Modifier,
    spacedBy: Dp = 8.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val subjectColor = MaterialTheme.colorScheme.subjectColorFrom(type)

    val bgModifier = remember(state, subjectColor) {
        if (state == ScheduleItem.State.Active) {
            Modifier.background(
                Brush.linearGradient(
                    listOf(subjectColor.copy(alpha = 0.2f), Color.Transparent)
                )
            )
        } else {
            Modifier
        }
    }

    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(bgModifier)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacedBy)
    ) {
        if (index.length < 2) {
            Index(value = index, modifier = indexModifier, color = indexColor(state))
        } else {
            TimeIndex(value = index, modifier = indexModifier, color = indexColor(state))
        }

        TypeIndicator(
            color = if (state == ScheduleItem.State.Expired) subjectColor.copy(alpha = 0.6f) else subjectColor,
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
private fun indexColor(state: ScheduleItem.State): Color {
    return if (state == ScheduleItem.State.Expired) {
        MaterialTheme.colorScheme.outline
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
}

@Composable
private fun Index(
    value: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = value,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun TimeIndex(
    value: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = value,
        fontWeight = FontWeight.Bold,
        color = color,
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
                onClick = { },
                state = ScheduleItem.State.Pending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
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
                onClick = { },
                state = ScheduleItem.State.Active,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
            ) {
                Text(text = "Content")
            }
        }
    }
}
