package com.subreax.schedule.ui.component.schedule.item.subject

import android.content.res.Configuration
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.utils.runOnEachMinute
import com.subreax.schedule.ui.theme.ScheduleTheme


@Composable
fun SubjectItem(
    item: ScheduleItem.Subject,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
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
        onClick = { onSubjectClicked(item) },
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
    indexModifier: Modifier = Modifier,
    spacedBy: Dp = 8.dp
) {
    BaseSubjectItem(
        index = index,
        type = type,
        onClick = onClick,
        state = state,
        modifier = modifier.height(IntrinsicSize.Max),
        indexModifier = indexModifier,
        spacedBy = spacedBy
    ) {
        CompositionLocalProvider(LocalContentColor provides titleColor(state)) {
            SubjectTitle(title = title, note = note)
        }

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = subtitleColor(state),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
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
private fun titleColor(state: ScheduleItem.State): Color {
    return if (state != ScheduleItem.State.Expired) {
        LocalContentColor.current
    } else {
        MaterialTheme.colorScheme.outline
    }
}

@Composable
private fun subtitleColor(state: ScheduleItem.State): Color {
    return if (state != ScheduleItem.State.Expired) {
        MaterialTheme.colorScheme.outline
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
}



@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SubjectItemPreview() {
    ScheduleTheme {
        Surface {
            SubjectItem(
                index = "2",
                title = "Заголовоккккк",
                subtitle = "Подзаголовок",
                type = SubjectType.Lab,
                note = null,
                onClick = { },
                state = ScheduleItem.State.Pending,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
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
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}