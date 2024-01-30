package com.subreax.schedule.ui.component.scheduleitemlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.component.LoadingIndicator
import com.subreax.schedule.ui.component.scheduleitemlist.subjectitem.SubjectItem

private val subjectModifier = Modifier
    .padding(horizontal = 16.dp, vertical = 8.dp)
    .fillMaxWidth()

private val titleModifier = Modifier.padding(
    start = 16.dp,
    end = 16.dp,
    top = 32.dp,
    bottom = 8.dp
)


@Composable
fun ScheduleItemList(
    isLoading: Boolean,
    items: List<ScheduleItem>,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        LoadingIndicator(isLoading = isLoading, modifier = Modifier.align(Alignment.Center))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items.forEach {
                when (it) {
                    is ScheduleItem.Subject -> {
                        item(it.id) {
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
                        // todo: is it required to use key in this place?
                        item {
                            TitleItem(
                                title = it.title,
                                modifier = titleModifier
                            )
                        }
                    }
                }
            }
        }
    }
}
