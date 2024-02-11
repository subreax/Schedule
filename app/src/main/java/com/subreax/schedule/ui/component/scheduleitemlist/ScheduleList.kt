package com.subreax.schedule.ui.component.scheduleitemlist

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.component.LoadingContainer
import com.subreax.schedule.ui.component.scheduleitemlist.subjectitem.SubjectItem
import com.subreax.schedule.ui.theme.ScheduleTheme

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
fun ScheduleList(
    isLoading: Boolean,
    items: List<ScheduleItem>,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
    modifier: Modifier = Modifier
) {
    LoadingContainer(isLoading = isLoading, modifier = modifier) {
        if (items.isNotEmpty()) {
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
        } else {
            NoLessonsLabel(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .fillMaxHeight(0.5f)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun NoLessonsLabel(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(
            Icons.Outlined.Celebration,
            "",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )

        Text(
            text = "УраааАААЫА\nпары кончились",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}


@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ScheduleListPreview() {
    ScheduleTheme {
        Surface {
            ScheduleList(
                isLoading = false,
                items = emptyList(),
                onSubjectClicked = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
