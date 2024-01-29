package com.subreax.schedule.ui.component.scheduleitemlist.subjectitem

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
    modifier: Modifier = Modifier
) {
    BaseSubjectItem(
        index = index,
        type = type,
        onClick = onClick,
        modifier = modifier
    ) {
        SubjectTitle(title = title, note = note)

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
private fun SubjectTitle(title: String, note: String?) {
    if (note != null) {
        TitleLayout(
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            note = {
                val text = remember { "($note)" }
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
    else {
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SubjectItemPreview() {
    ScheduleTheme {
        Surface {
            SubjectItem(
                index = "2",
                title = "Заголовок",
                subtitle = "Подзаголовок",
                type = SubjectType.Lab,
                note = "примечание",
                onClick = {  },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}