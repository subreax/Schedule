package com.subreax.schedule.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.Group
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.component.TypeIndicator
import com.subreax.schedule.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailsBottomSheet(
    name: String,
    nameAlias: String,
    type: SubjectType,
    teacher: String,
    date: String,
    time: String,
    place: String,
    groups: List<Group>,
    note: String,
    onIdClicked: (String) -> Unit,
    onDismiss: () -> Unit,
    onRenameClicked: (() -> Unit)? = null,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets(0.dp),
        dragHandle = { DragHandle(modifier = Modifier.padding(vertical = 8.dp)) }
    ) {
        SubjectDetailsContent(
            originalName = name,
            nameAlias = nameAlias,
            type = type,
            teacher = teacher,
            date = date,
            time = time,
            place = place,
            groups = groups,
            note = note,
            onIdClicked = onIdClicked,
            onRenameClicked = onRenameClicked
        )

        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun DragHandle(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(0.4f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            Modifier
                .size(
                    width = 48.dp,
                    height = 4.dp
                )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SubjectDetailsContent(
    originalName: String,
    nameAlias: String,
    type: SubjectType,
    teacher: String,
    date: String,
    time: String,
    place: String,
    groups: List<Group>,
    note: String,
    onIdClicked: (String) -> Unit,
    onRenameClicked: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Header(
            originalName = originalName,
            nameAlias = nameAlias,
            note = note,
            type = type,
            date = date,
            time = time,
            onRenameClicked = onRenameClicked,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Column(
            modifier = Modifier.padding(
                start = 12.dp,
                end = 12.dp,
                top = 24.dp,
                bottom = 16.dp
            )
        ) {
            if (teacher.isNotBlank()) {
                ChipItem(
                    text = teacher,
                    onClick = { onIdClicked(teacher) }
                )
            } else {
                ChipItem(
                    text = "Преподаватель не указан",
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (groups.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    groups.forEach {
                        val text = remember(it.id) {
                            if (it.note.isEmpty()) {
                                it.id
                            } else {
                                "${it.id} (${it.note})"
                            }
                        }
                        ChipItem(
                            text = text,
                            onClick = { onIdClicked(it.id) }
                        )
                    }
                }
            }

            ChipItem(
                text = place,
                onClick = { onIdClicked(place) },
                modifier = Modifier.padding(top = 4.dp)
            )

            /*Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChipItem(text = place, onClick = { onIdClicked(place) })
    
                TextButton(
                    onClick = {  },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(text = "Показать на карте")
                    Icon(Icons.Filled.ChevronRight, "")
                }
            }*/
        }
    }
}

@Composable
private fun Header(
    originalName: String,
    nameAlias: String,
    note: String,
    type: SubjectType,
    date: String,
    time: String,
    onRenameClicked: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val nameInTitle = nameAlias.ifEmpty { originalName }
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
                    text = originalName,
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

@Composable
private fun ChipItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                CircleShape
            )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(icon, "")
        Text(text = text)
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SubjectDetailsBottomSheetPreview() {
    ScheduleTheme {
        Surface {
            SubjectDetailsContent(
                originalName = "Имя предмета",
                nameAlias = "Псевдоним",
                type = SubjectType.Lecture,
                teacher = "Преподаватель И. О.",
                date = "25.05.2024",
                time = "17:12",
                place = "Место",
                groups = emptyList(),
                note = "Примечание",
                onIdClicked = {},
                onRenameClicked = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun TitlePreview() {
    ScheduleTheme {
        Surface {
            Header(
                originalName = "Имя",
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

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ChipItemPreview() {
    ScheduleTheme {
        Surface {
            ChipItem(text = "Item", onClick = { }, modifier = Modifier.padding(16.dp))
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SettingItemPreview() {
    ScheduleTheme {
        Surface {
            SettingItem(
                icon = Icons.Filled.Edit,
                text = "Переименовать",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}