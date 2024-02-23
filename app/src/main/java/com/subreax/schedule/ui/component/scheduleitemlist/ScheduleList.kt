package com.subreax.schedule.ui.component.scheduleitemlist

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.component.ListPopupButton
import com.subreax.schedule.ui.component.LoadingContainer
import com.subreax.schedule.ui.component.scheduleitemlist.subjectitem.SubjectItem
import com.subreax.schedule.ui.theme.ScheduleTheme
import kotlinx.coroutines.launch
import java.util.Date

private val subjectModifier = Modifier
    .padding(horizontal = 16.dp, vertical = 8.dp)
    .fillMaxWidth()

private val titleModifier = Modifier.padding(
    start = 16.dp,
    end = 16.dp,
    top = 32.dp,
    bottom = 8.dp
)

private val labelModifier = Modifier
    .fillMaxWidth(0.7f)
    .fillMaxHeight(0.5f)

@Composable
fun ScheduleList(
    items: List<ScheduleItem>,
    todayItemIndex: Int,
    isLoading: Boolean,
    failedToLoad: Boolean,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    val scope = rememberCoroutineScope()

    LoadingContainer(isLoading = isLoading, modifier = modifier) {
        if (items.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 92.dp),
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
                                val textColor = if (i != todayItemIndex) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }

                                val lineColor = if (i != todayItemIndex) {
                                    MaterialTheme.colorScheme.outline
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }

                                TitleItem(
                                    title = it.title,
                                    modifier = titleModifier,
                                    textColor = textColor,
                                    lineColor = lineColor
                                )
                            }
                        }
                    }
                }
            }

            AutoShownScrollToStartButton(
                todayItemIndex = todayItemIndex,
                listState = listState,
                onClick = {
                    scope.launch { listState.animateScrollToItem(todayItemIndex) }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        } else if (failedToLoad) {
            FailedToLoadScheduleLabel(labelModifier.align(Alignment.Center))
        } else {
            NoLessonsLabel(labelModifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun NoLessonsLabel(modifier: Modifier = Modifier) {
    Label(
        icon = Icons.Outlined.Celebration,
        text = "УраааАААЫА\nпары кончились",
        modifier = modifier
    )
}

@Composable
private fun FailedToLoadScheduleLabel(modifier: Modifier = Modifier) {
    Label(
        icon = Icons.Filled.Close,
        text = "Не удалось загрузить расписание",
        modifier = modifier
    )
}

@Composable
private fun Label(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(
            icon,
            "",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )

        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun AutoShownScrollToStartButton(
    todayItemIndex: Int,
    listState: LazyListState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArrowUpwardShown by remember {
        derivedStateOf { listState.firstVisibleItemIndex > todayItemIndex }
    }

    val isArrowDownwardShown by remember {
        derivedStateOf { listState.firstVisibleItemIndex + 8 < todayItemIndex }
    }

    AnimatedBottomShadowContainer(visible = isArrowUpwardShown, modifier = modifier) {
        ScrollToStartButton(icon = Icons.Filled.ExpandLess, onClick = onClick)
    }

    AnimatedBottomShadowContainer(visible = isArrowDownwardShown, modifier = modifier) {
        ScrollToStartButton(icon = Icons.Filled.ExpandMore, onClick = onClick)
    }
}

@Composable
fun AnimatedBottomShadowContainer(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it },
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                    )
                )
                .fillMaxWidth()
                .height(96.dp)
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            content()
        }
    }
}

@Composable
fun ScrollToStartButton(icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListPopupButton(onClick = onClick, modifier = modifier) {
        Text("Вернуться")
        Icon(
            imageVector = icon,
            contentDescription = "Вернуться в начало",
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}


@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ScheduleListPreview() {
    ScheduleTheme {
        Surface {
            ScheduleList(
                items = listOf(
                    ScheduleItem.Title("Сегодня", Date()),
                    ScheduleItem.Subject(
                        id = 0,
                        index = "1",
                        title = "Предмет",
                        subtitle = "Подзаголовок",
                        type = SubjectType.Lecture,
                        note = null,
                        date = Date(),
                    )
                ),
                todayItemIndex = -1,
                isLoading = false,
                failedToLoad = false,
                onSubjectClicked = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
