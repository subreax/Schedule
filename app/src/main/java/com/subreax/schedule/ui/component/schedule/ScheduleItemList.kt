package com.subreax.schedule.ui.component.schedule

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.ui.component.schedule.item.subject.SubjectItem
import com.subreax.schedule.ui.component.schedule.item.title.TitleItem
import com.subreax.schedule.ui.theme.ScheduleTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Date

private const val HOUR = 1000 * 60 * 60L

private val titleModifier = Modifier
    .padding(
        start = 16.dp,
        end = 16.dp,
        top = 24.dp,
        bottom = 8.dp
    )
    .fillMaxWidth()

private val subjectModifier = Modifier
    .padding(horizontal = 16.dp, vertical = 8.dp)
    .fillMaxWidth()

private val indexModifier = Modifier.widthIn(22.dp)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleItemList(
    items: List<ScheduleItem>,
    todayItemIndex: Int,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    listState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        state = listState
    ) {
        items.forEachIndexed { i, item ->
            when (item) {
                is ScheduleItem.Subject -> {
                    item(key = item.id, contentType = 1) {
                        SubjectItem(
                            item = item,
                            onSubjectClicked = onSubjectClicked
                        )
                    }
                }

                is ScheduleItem.Title -> {
                    stickyHeader(key = item.date.time, contentType = 2) {
                        Surface {
                            TitleItem(
                                title = item.title,
                                highlighted = i == todayItemIndex,
                                modifier = titleModifier,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectItem(
    item: ScheduleItem.Subject,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit
) {
    var remaining by remember {
        mutableIntStateOf(item.calculateRemainingMinutesOrZero())
    }

    // Текущий и будущие предметы в пределах некоторого времени будут определять,
    // сколько минут до конца (и активны ли они)
    if (item.couldBeActive()) {
        LaunchedEffect(item) {
            while (isActive) {
                delay(5000)
                remaining = item.calculateRemainingMinutesOrZero()
            }
        }
    }

    val isSubjectActive by remember {
        derivedStateOf { remaining > 0 }
    }

    Column {
        if (isSubjectActive) {
            ActiveLabel(
                minutesRemaining = remaining,
                modifier = Modifier.padding(
                    start = 21.dp,
                    bottom = 2.dp,
                    top = 8.dp
                )
            )
        }

        SubjectItem(
            index = item.index,
            title = item.title,
            subtitle = item.subtitle,
            type = item.type,
            note = item.note,
            onClick = { onSubjectClicked(item) },
            isActive = isSubjectActive,
            modifier = subjectModifier,
            indexModifier = indexModifier
        )
    }
}

@Composable
fun ActiveLabel(
    minutesRemaining: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            Icons.Filled.HourglassBottom,
            "",
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(12.dp)
        )

        Text(
            text = "Осталось $minutesRemaining мин.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScheduleItemListPreview() {
    val items = listOf(
        ScheduleItem.Title("Сегодня", Date()),
        ScheduleItem.Subject(
            1,
            "2",
            Date(),
            "Предмет 1",
            "Подзаголовок",
            SubjectType.Lecture,
            null,
            Date(System.currentTimeMillis() + 5000000)
        ),
        ScheduleItem.Subject(
            2,
            "13\n40",
            Date(System.currentTimeMillis() + 10000000),
            "Предмет 2",
            "Подзаголовок",
            SubjectType.Practice,
            null,
            Date(System.currentTimeMillis() + 15000000)
        )
    )

    ScheduleTheme {
        Surface {
            ScheduleItemList(items = items, todayItemIndex = 0, onSubjectClicked = {})
        }
    }
}

private fun ScheduleItem.Subject.couldBeActive(): Boolean {
    val t = end.time - System.currentTimeMillis()
    return t >= 0 && t <= (8 * HOUR)
}