package com.subreax.schedule.ui.component.schedule.item.subject

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.component.TypeIndicator
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.ui.theme.ScheduleTheme
import com.subreax.schedule.ui.theme.scheduleColors
import com.subreax.schedule.utils.runOnEachMinute


private val typeIndicatorModifier = Modifier
    .padding(vertical = 2.dp)
    .width(4.dp)
    .fillMaxHeight()

private const val disabledAlpha = 0.5f
private const val highlightAlpha = 0.2f


@Composable
fun SubjectItem(
    item: ScheduleItem.Subject,
    onSubjectClicked: () -> Unit,
    modifier: Modifier,
    indexModifier: Modifier,
) {
    var state by remember { mutableStateOf(item.state) }

    LaunchedEffect(item) {
        runOnEachMinute { loop ->
            if (state == ScheduleItem.State.Expired) {
                loop.stop()
            } else {
                state = item.state
            }
        }
    }

    SubjectItem(
        index = item.index,
        title = item.title,
        subtitle = item.subtitle,
        type = item.type,
        note = item.note,
        onClick = onSubjectClicked,
        state = state,
        modifier = modifier,
        indexModifier = indexModifier
    )
}


@Composable
fun SubjectItem(
    index: String,
    title: String,
    subtitle: String,
    type: SubjectType,
    note: String?,
    onClick: () -> Unit,
    state: ScheduleItem.State,
    modifier: Modifier = Modifier,
    indexModifier: Modifier = Modifier
) {
    val subjectColor = MaterialTheme.scheduleColors.getSubjectColor(type)
    val isActive = state == ScheduleItem.State.Active

    val alphaModifier = if (state == ScheduleItem.State.Expired)
        Modifier.alpha(disabledAlpha)
    else
        Modifier

    val bgModifier = remember(isActive, subjectColor) {
        if (isActive) {
            Modifier.background(
                Brush.linearGradient(
                    listOf(subjectColor.copy(alpha = highlightAlpha), Color.Transparent)
                )
            )
        } else {
            Modifier
        }
    }

    BaseSubjectItem(
        index = {
            if (index.length < 2) {
                Index(value = index, modifier = indexModifier)
            } else {
                TimeIndex(value = index, modifier = indexModifier)
            }
        },
        typeIndicator = {
            TypeIndicator(
                color = subjectColor,
                modifier = typeIndicatorModifier
            )
        },
        content = {
            SubjectTitle(title = title, note = note)
            SubjectSubtitle(text = subtitle)
        },
        onClick = onClick,
        modifier = bgModifier
            .then(modifier)
            .height(IntrinsicSize.Max)
            .then(alphaModifier)

    )
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

@Composable
private fun SubjectTitle(title: String, note: String?) {
    TitleLayout(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        note = {
            if (note != null) {
                val text = remember { "($note)" }
                Text(
                    text = text,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    )
}

@Composable
private fun SubjectSubtitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.outline,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}


@PreviewLightDark
@Composable
private fun SubjectItemPreview() {
    ScheduleTheme {
        Surface {
            Column {
                SubjectItem(
                    index = "1",
                    title = "Заголовоккккк",
                    subtitle = "Подзаголовок",
                    type = SubjectType.Lab,
                    note = null,
                    onClick = { },
                    state = ScheduleItem.State.Expired,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                SubjectItem(
                    index = "2",
                    title = "Заголовоккккк",
                    subtitle = "Подзаголовок",
                    type = SubjectType.Lab,
                    note = null,
                    onClick = { },
                    state = ScheduleItem.State.Pending,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun SubjectItemNotePreview() {
    ScheduleTheme {
        Surface {
            SubjectItem(
                index = "2",
                title = "Заголовок",
                subtitle = "Подзаголовок",
                type = SubjectType.Lab,
                note = "примечание",
                onClick = { },
                state = ScheduleItem.State.Pending,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}