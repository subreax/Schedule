package com.subreax.schedule.ui.component.schedule

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.ui.component.schedule.item.title.TitleItem
import com.subreax.schedule.ui.theme.ScheduleTheme
import java.util.Date


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
                    item(key = item.id, contentType = ScheduleItem.Subject.ContentType) {
                        SubjectItem2(
                            item = item,
                            onSubjectClicked = onSubjectClicked,
                            modifier = subjectModifier,
                            indexModifier = indexModifier
                        )
                    }
                }

                is ScheduleItem.Title -> {
                    stickyHeader(
                        key = item.begin.time,
                        contentType = ScheduleItem.Title.ContentType
                    ) {
                        Surface {
                            TitleItem(
                                title = item.title,
                                highlighted = i == todayItemIndex,
                                modifier = titleModifier,
                            )
                        }
                    }
                }

                is ScheduleItem.PendingLabel -> {
                    item(
                        key = item.begin.time,
                        contentType = ScheduleItem.PendingLabel.ContentType
                    ) {
                        PendingLabel(
                            item = item,
                            modifier = Modifier.padding(top = 10.dp, start = 42.dp, bottom = 1.dp)
                        )
                    }
                }

                is ScheduleItem.ActiveLabel -> {
                    item(
                        key = item.begin.time,
                        contentType = ScheduleItem.ActiveLabel.ContentType
                    ) {
                        ActiveLabel(
                            item = item,
                            modifier = Modifier.padding(top = 10.dp, start = 42.dp, bottom = 1.dp)
                        )
                    }
                }

                else -> {
                    throw Exception("Unknown item: ${item.javaClass.simpleName}")
                }
            }
        }
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
            Date(System.currentTimeMillis() + 5000000),
            "Предмет 1",
            "Подзаголовок",
            SubjectType.Lecture,
            null,
        ),
        ScheduleItem.Subject(
            2,
            "13\n40",
            Date(System.currentTimeMillis() + 10000000),
            Date(System.currentTimeMillis() + 15000000),
            "Предмет 2",
            "Подзаголовок",
            SubjectType.Practice,
            null,
        )
    )

    ScheduleTheme {
        Surface {
            ScheduleItemList(items = items, todayItemIndex = 0, onSubjectClicked = {})
        }
    }
}
