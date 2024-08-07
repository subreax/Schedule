package com.subreax.schedule.ui.component.schedule

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.ui.component.schedule.item.subject.SubjectItem
import com.subreax.schedule.ui.component.schedule.item.title.TitleItem

private val titleModifier = Modifier.padding(
    start = 16.dp,
    end = 16.dp,
    top = 32.dp,
    bottom = 8.dp
)

private val subjectModifier = Modifier
    .padding(horizontal = 16.dp, vertical = 8.dp)
    .fillMaxWidth()

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
        items.forEachIndexed { i, it ->
            when (it) {
                is ScheduleItem.Subject -> {
                    item(key = it.id, contentType = 1) {
                        SubjectItem(
                            index = it.index,
                            title = it.title,
                            subtitle = it.subtitle,
                            type = it.type,
                            note = it.note,
                            onClick = { onSubjectClicked(it) },
                            modifier = subjectModifier
                        )
                    }
                }

                is ScheduleItem.Title -> {
                    item(key = null, contentType = 2) {
                        TitleItem(
                            title = it.title,
                            highlighted = i == todayItemIndex,
                            modifier = titleModifier,
                        )
                    }
                }
            }
        }
    }
}