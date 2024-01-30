package com.subreax.schedule.ui.home

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.Group
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.component.TypeIndicator

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
                                "${it.id} (${it.note}"
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
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TypeIndicator(
                type = type,
                modifier = Modifier.size(8.dp),
            )
            Text(
                text = type.name,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = "$date, $time",
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
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

