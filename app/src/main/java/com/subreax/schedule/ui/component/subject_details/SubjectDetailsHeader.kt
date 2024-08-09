package com.subreax.schedule.ui.component.subject_details

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.component.TypeIndicator
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun SubjectDetailsHeader(
    name: String,
    nameAlias: String,
    note: String,
    type: SubjectType,
    date: String,
    time: String,
    onRenameClicked: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val nameInTitle = nameAlias.ifEmpty { name }
    val title = if (note.isEmpty()) nameInTitle else "$nameInTitle ($note)"

    Row(modifier) {
        Column(
            Modifier
                .weight(1f)
                .padding(top = 8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (nameAlias.isNotEmpty()) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                )
            }

            SubjectTypeLabel(type = type, modifier = Modifier.padding(top = 8.dp))

            DateTimeLabel(
                date = date,
                time = time,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        onRenameClicked?.let { onRenameClickedCallback ->
            IconButton(onClick = onRenameClickedCallback) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}


@Composable
private fun SubjectTypeLabel(type: SubjectType, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        TypeIndicator(
            type = type,
            modifier = Modifier
                .size(16.dp)
                .padding(4.dp),
        )
        Text(
            text = type.name,
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DateTimeLabel(
    date: String,
    time: String,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        InfoItem(icon = Icons.Filled.CalendarToday, text = date)
        InfoItem(
            icon = Icons.Filled.Schedule,
            text = time,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun InfoItem(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.outline,
        )

        Text(
            text = text,
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SubjectDetailsHeaderPreview() {
    ScheduleTheme {
        Surface {
            SubjectDetailsHeader(
                name = "Имя",
                nameAlias = "",
                note = "прим",
                type = SubjectType.Lecture,
                date = "04.02.2024",
                time = "18:38",
                onRenameClicked = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SubjectDetailsHeaderWithAliasPreview() {
    ScheduleTheme {
        Surface {
            SubjectDetailsHeader(
                name = "Имя",
                nameAlias = "Псевдоним",
                note = "прим",
                type = SubjectType.Lecture,
                date = "04.02.2024",
                time = "18:38",
                onRenameClicked = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}