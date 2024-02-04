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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailsBottomSheet(
    name: String,
    type: SubjectType,
    teacher: String,
    date: String,
    time: String,
    place: String,
    groups: List<Group>,
    note: String,
    onIdClicked: (String) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets(0.dp)
    ) {
        Title(
            name = name,
            note = note,
            type = type,
            date = date,
            time = time,
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
            ChipItem(
                text = teacher,
                onClick = { onIdClicked(teacher) }
            )

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

            Spacer(Modifier.height(4.dp))

            // todo: разобраться с модификаторами, чтобы padding можно было применить на него
            ChipItem(
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

        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun Title(
    name: String,
    note: String,
    type: SubjectType,
    date: String,
    time: String,
    modifier: Modifier = Modifier
) {
    val title = if (note.isEmpty()) name else "$name ($note)"

    Column(modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 8.dp)
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

        Row(Modifier.padding(top = 4.dp)) {
            InfoItem(icon = Icons.Filled.CalendarToday, text = date)
            InfoItem(
                icon = Icons.Filled.Schedule,
                text = time,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
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
        Modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .then(modifier)
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                RoundedCornerShape(50)
            )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun TitlePreview() {
    ScheduleTheme {
        Surface {
            Title(
                name = "Имя",
                note = "прим",
                type = SubjectType.Lecture,
                date = "04.02.2024",
                time = "18:38",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}
