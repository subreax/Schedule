package com.subreax.schedule.ui.component.schedule

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.subreax.schedule.data.model.TimeRange
import com.subreax.schedule.ui.component.schedule.item.ActiveLabel
import com.subreax.schedule.ui.component.schedule.item.PendingLabel
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.ui.component.schedule.item.subject.SubjectItem
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
        items.forEach { item ->
            when (item) {
                is ScheduleItem.Subject -> {
                    item(key = item.key, contentType = ScheduleItem.Subject.ContentType) {
                        SubjectItem(
                            item = item,
                            onSubjectClicked = onSubjectClicked,
                            modifier = subjectModifier,
                            indexModifier = indexModifier
                        )
                    }
                }

                is ScheduleItem.Title -> {
                    stickyHeader(
                        key = item.key,
                        contentType = ScheduleItem.Title.ContentType
                    ) {
                        Surface {
                            TitleItem(
                                title = item.title,
                                state = item.state,
                                modifier = titleModifier,
                            )
                        }
                    }
                }

                is ScheduleItem.PendingLabel -> {
                    item(
                        key = item.key,
                        contentType = ScheduleItem.PendingLabel.ContentType
                    ) {
                        PendingLabel(
                            item = item,
                            modifier = Modifier.padding(top = 10.dp, start = 21.dp, bottom = 1.dp)
                        )
                    }
                }

                is ScheduleItem.ActiveLabel -> {
                    item(
                        key = item.key,
                        contentType = ScheduleItem.ActiveLabel.ContentType
                    ) {
                        ActiveLabel(
                            item = item,
                            modifier = Modifier.padding(top = 10.dp, start = 21.dp, bottom = 1.dp)
                        )
                    }
                }

                is ScheduleItem.Mark -> {
                    item(key = item.key, contentType = ScheduleItem.Mark.ContentType) {
                        Spacer(Modifier.height(1.dp).fillMaxWidth())
                    }
                }
            }
        }
    }
}




@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScheduleItemListPreview() {
    val activeTimeRange = TimeRange(Date(), Date(System.currentTimeMillis() + 5000000))

    val items = listOf(
        ScheduleItem.Title(
            key = 100,
            "Сегодня, 99.99",
            TimeRange(Date(), Date(System.currentTimeMillis() + 10000000))
        ),
        ScheduleItem.ActiveLabel(key = 0, activeTimeRange),
        ScheduleItem.Subject(
            1,
            "2",
            activeTimeRange,
            "Предмет 1",
            "Подзаголовок",
            SubjectType.Lecture,
            null,
        ),
        ScheduleItem.Subject(
            2,
            "13\n40",
            TimeRange(
                Date(System.currentTimeMillis() + 10000000),
                Date(System.currentTimeMillis() + 15000000)
            ),
            "Предмет 2",
            "Подзаголовок",
            SubjectType.Practice,
            null,
        )
    )

    ScheduleTheme {
        Surface {
            ScheduleItemList(items = items, onSubjectClicked = {})
        }
    }
}
