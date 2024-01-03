package com.subreax.schedule.ui.details

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.component.TypeIndicator
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun SubjectDetailsScreen(
    navBack: () -> Unit,
    detailsViewModel: DetailsViewModel = hiltViewModel()
) {
    val subject = detailsViewModel.subject

    SubjectDetailsScreen(
        name = subject.name,
        type = subject.type,
        teacher = subject.teacher,
        date = subject.date,
        time = subject.time,
        place = subject.place,
        navBack = navBack
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailsScreen(
    name: String,
    type: SubjectType,
    teacher: String,
    date: String,
    time: String,
    place: String,
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
            type = type,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 32.dp)
        )

        DetailItem(
            title = "Преподаватель",
            subtitle = teacher,
            icon = { Icon(Icons.Filled.School, "") },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        DetailItem(
            title = "Дата",
            subtitle = date,
            icon = { Icon(Icons.Filled.CalendarToday, "") },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        DetailItem(
            title = "Время",
            subtitle = time,
            icon = { Icon(Icons.Filled.Schedule, "") },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        DetailItem(
            title = "Место проведения",
            subtitle = place,
            icon = { Icon(Icons.Filled.LocationOn, "") },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun Title(
    name: String,
    type: SubjectType,
    modifier: Modifier = Modifier
) {
    var rowHeight by remember { mutableIntStateOf(0) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.onSizeChanged { rowHeight = it.height }
    ) {
        with(LocalDensity.current) {
            TypeIndicator(
                type = type,
                modifier = Modifier
                    .height(rowHeight.toDp())
                    .width(5.dp)
                    .padding(vertical = 4.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = type.name,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun DetailItem(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.outline) {
            icon()
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(subtitle)
        }
    }
}

@Preview(widthDp = 400, heightDp = 720, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SubjectDetailsScreenPreview() {
    ScheduleTheme {
        Surface {
            SubjectDetailsScreen(
                name = "Математический анал",
                type = SubjectType.Lecture,
                teacher = "Кузнецова Валентина АнатольевнаАЫАыфАыф",
                date = "25 ноября 2023",
                time = "13:40 - 15:15",
                place = "Гл.-431",
                navBack = {}
            )
        }
    }
}

@Preview(widthDp = 300, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DetailItemPreview() {
    ScheduleTheme {
        Surface {
            DetailItem(
                title = "Название",
                subtitle = "Математический анал",
                icon = { Icon(Icons.Filled.School, "") },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

