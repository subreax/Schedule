package com.subreax.schedule.ui.component.subject_details

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.component.SChoiceChip
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
    groups: List<GroupAndBookmark>,
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
            name = name,
            nameAlias = nameAlias,
            type = type,
            teacher = teacher,
            date = date,
            time = time,
            place = place,
            groupsWithBookmarks = groups,
            note = note,
            onIdClicked = onIdClicked,
            onRenameClicked = onRenameClicked,
            modifier = Modifier
                .padding(bottom = 8.dp, start = 12.dp, end = 12.dp)
                .verticalScroll(rememberScrollState())
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
    name: String,
    nameAlias: String,
    type: SubjectType,
    teacher: String,
    date: String,
    time: String,
    place: String,
    groupsWithBookmarks: List<GroupAndBookmark>,
    note: String,
    onIdClicked: (String) -> Unit,
    onRenameClicked: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        SubjectDetailsHeader(
            name = name,
            nameAlias = nameAlias,
            note = note,
            type = type,
            date = date,
            time = time,
            onRenameClicked = onRenameClicked,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 16.dp)
        )

        if (teacher.isNotBlank()) {
            SChoiceChip(
                text = teacher,
                onClick = { onIdClicked(teacher) }
            )
        } else {
            SChoiceChip(
                text = "Преподаватель не указан",
                onClick = { },
                enabled = false
            )
        }

        if (groupsWithBookmarks.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                groupsWithBookmarks.forEach {
                    SChoiceChip(
                        text = it.asPrettyString(),
                        onClick = { onIdClicked(it.group.id) },
                        highlighted = it.bookmark != null
                    )
                }
            }
        }

        SChoiceChip(
            text = place,
            onClick = { onIdClicked(place) }
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

private fun GroupAndBookmark.asPrettyString(): String {
    val id = bookmark?.nameOrId() ?: group.id

    return if (group.note == null) {
        id
    } else {
        "$id (${group.note})"
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SubjectDetailsContentPreview() {
    ScheduleTheme {
        Surface {
            SubjectDetailsContent(
                name = "Имя предмета",
                nameAlias = "Псевдоним",
                type = SubjectType.Lecture,
                teacher = "Преподаватель И. О.",
                date = "25.05.2024",
                time = "17:12",
                place = "Место",
                groupsWithBookmarks = emptyList(),
                note = "Примечание",
                onIdClicked = {},
                onRenameClicked = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
