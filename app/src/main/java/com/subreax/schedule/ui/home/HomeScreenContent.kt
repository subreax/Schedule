package com.subreax.schedule.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.component.LoadingIndicator

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
fun HomeScreenContent(
    isLoading: Boolean,
    schedule: List<HomeViewModel.ScheduleItem>,
    onSubjectClicked: (HomeViewModel.ScheduleItem.Subject) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        LoadingIndicator(isLoading = isLoading, modifier = Modifier.align(Alignment.Center))

        LazyColumn(Modifier.fillMaxSize()) {
            schedule.forEach {
                when (it) {
                    is HomeViewModel.ScheduleItem.Subject -> {
                        item(it.id) {
                            SubjectItem(
                                index = it.index,
                                name = it.name,
                                infoItem1 = it.place,
                                infoItem2 = it.teacherName,
                                type = it.type,
                                note = it.note,
                                onSubjectClicked = { onSubjectClicked(it) },
                                modifier = subjectModifier
                            )
                        }
                    }

                    is HomeViewModel.ScheduleItem.Title -> {
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
