package com.subreax.schedule.ui.component.schedule.item.subject

import android.content.res.Configuration
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun SubjectItem(
    index: String,
    title: String,
    subtitle: String,
    type: SubjectType,
    note: String?,
    onClick: () -> Unit,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    indexModifier: Modifier = Modifier,
    spacedBy: Dp = 8.dp
) {
    BaseSubjectItem(
        index = index,
        type = type,
        onClick = onClick,
        isActive = isActive,
        modifier = modifier.height(IntrinsicSize.Max),
        indexModifier = indexModifier,
        spacedBy = spacedBy
    ) {
        SubjectTitle(title = title, note = note)

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
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
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    )
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
                isActive = false,
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
                isActive = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}