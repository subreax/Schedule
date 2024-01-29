package com.subreax.schedule.ui.details

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.Group
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.BaseScheduleViewModel
import com.subreax.schedule.ui.component.TypeIndicator
import com.subreax.schedule.ui.theme.ScheduleTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Composable
fun SubjectDetailsScreen(
    scheduleViewModel: BaseScheduleViewModel,
    onIdClicked: (String) -> Unit,
    navBack: () -> Unit
) {
    val subject = scheduleViewModel.pickedSubject!!
    val owner = scheduleViewModel.currentScheduleOwner

    val date = remember(subject.id) {
        formatDate(subject.timeRange.start)
    }

    val time = remember(subject.id) {
        subject.timeRange.toString(Calendar.getInstance())
    }

    val groups = remember(subject.id) {
        if (owner.type != ScheduleOwner.Type.Student) {
            subject.groups
        } else {
            emptyList()
        }
    }

    val note = remember(subject.id) {
        if (owner.type == ScheduleOwner.Type.Student) {
            subject.groups.first().note
        } else {
            ""
        }
    }

    SubjectDetailsScreen(
        name = subject.name,
        type = subject.type,
        teacher = subject.teacher?.full() ?: "Не указано",
        date = date,
        time = time,
        place = subject.place,
        groups = groups,
        note = note,
        onIdClicked = onIdClicked,
        navBack = navBack
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SubjectDetailsScreen(
    name: String,
    type: SubjectType,
    teacher: String,
    date: String,
    time: String,
    place: String,
    groups: List<Group>,
    note: String,
    onIdClicked: (String) -> Unit,
    navBack: () -> Unit
) {
    Column {
        TopAppBar(
            title = { Text(text = "Детали", style = MaterialTheme.typography.titleMedium) },
            navigationIcon = {
                IconButton(onClick = navBack) {
                    Icon(Icons.Filled.ArrowBack, "Nav back")
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                // todo: extract to somewhere
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
            )
        )

        Title(
            name = name,
            note = note,
            type = type,
            date = date,
            time = time,
            modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)
        )

        Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 24.dp)) {
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChipItem(text = place, onClick = { onIdClicked(place) })

                TextButton(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(text = "Показать на карте")
                    Icon(Icons.Filled.ChevronRight, "")
                }
            }
        }
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
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                RoundedCornerShape(50)
            )
            .then(modifier)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(widthDp = 400, heightDp = 720, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SubjectDetailsScreenPreview() {
    ScheduleTheme {
        Surface {
            SubjectDetailsScreen(
                name = "Введение в математический анализ",
                type = SubjectType.Lecture,
                teacher = "Кузнецова Валентина Анатольевна",
                date = "25 ноября 2023",
                time = "13:40 - 15:15",
                place = "Гл.-431",
                groups = listOf(
                    Group("220431"), Group("221131"), Group("220231"),
                    Group("220431"), Group("221131"), Group("220231")
                ),
                //groups = emptyList(),
                note = "",
                onIdClicked = {},
                navBack = {}
            )
        }
    }
}

private fun formatDate(date: Date): String {
    return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG).format(date)
}
