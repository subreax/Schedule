package com.subreax.schedule.ui.component.schedule

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyListItemInfo
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.R
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import com.subreax.schedule.ui.UiLoadingState
import com.subreax.schedule.ui.component.ListPopupButton
import com.subreax.schedule.ui.component.LoadingContainer
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.ui.theme.ScheduleTheme
import com.subreax.schedule.utils.toLocalizedString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

private val labelModifier = Modifier
    .fillMaxWidth(0.7f)
    .fillMaxHeight(0.5f)

@Composable
fun Schedule(
    items: List<ScheduleItem>,
    todayItemIndex: Int,
    loadingState: UiLoadingState,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val density = LocalDensity.current

    LoadingContainer(
        isLoading = loadingState is UiLoadingState.Loading,
        modifier = modifier,
        loadingText = stringResource(R.string.hacking_tsu_server),
        transitionSpec = {
            val initialOffset = with(density) { 32.dp.roundToPx() }
            (fadeIn() + slideInVertically { initialOffset })
                .togetherWith(fadeOut())
        }
    ) {
        if (items.isNotEmpty()) {
            ScheduleItemList(
                items = items,
                onSubjectClicked = onSubjectClicked,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 92.dp),
                listState = listState
            )

            AutoShownScrollToStartButton(
                todayItemIndex = todayItemIndex,
                listState = listState,
                onClick = {
                    coroutineScope.launch { listState.animateScrollToItem(todayItemIndex) }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        } else if (loadingState is UiLoadingState.Error) {
            FailedToLoadSchedule(
                message = loadingState.message.toLocalizedString(),
                modifier = labelModifier.align(Alignment.Center)
            )
        } else {
            NoLessons(labelModifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun NoLessons(modifier: Modifier = Modifier) {
    Message(
        icon = Icons.Outlined.Celebration,
        text = "Расписание отсутствует",
        modifier = modifier
    )
}

@Composable
private fun FailedToLoadSchedule(
    message: String,
    modifier: Modifier = Modifier
) {
    Message(
        icon = Icons.Filled.Close,
        text = message,
        modifier = modifier
    )
}

@Composable
private fun Message(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
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
private fun AutoShownScrollToStartButton(
    todayItemIndex: Int,
    listState: LazyListState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val arrowDirection by remember(listState, todayItemIndex) {
        derivedStateOf {
            val diff = listState.firstVisibleItemIndex - todayItemIndex
            val items = listState.layoutInfo.visibleItemsInfo
            var itemsCount = items.size
            // без этого кнопка будет странно себя вести, если прокрутить вверх.
            // это происходит из-за sticky header
            if (items.lastOrNull()?.isTitle() == true) {
                itemsCount -= 1
            }

            if (diff > 0) {
                1
            } else if (-diff + 2 > itemsCount) {
                -1
            } else {
                0
            }
        }
    }

    AnimatedBottomShadowContainer(visible = arrowDirection > 0, modifier = modifier) {
        ScrollToStartButton(icon = Icons.Filled.ExpandLess, onClick = onClick)
    }

    AnimatedBottomShadowContainer(visible = arrowDirection < 0, modifier = modifier) {
        ScrollToStartButton(icon = Icons.Filled.ExpandMore, onClick = onClick)
    }
}

private fun LazyListItemInfo.isTitle(): Boolean = contentType == ScheduleItem.Title.ContentType

@Composable
private fun AnimatedBottomShadowContainer(
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
private fun ScrollToStartButton(icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListPopupButton(onClick = onClick, modifier = modifier) {
        Text(stringResource(R.string.show_today))
        Icon(
            imageVector = icon,
            contentDescription = stringResource(R.string.show_today),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}


@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ScheduleListPreview() {
    ScheduleTheme {
        Surface {
            Schedule(
                items = listOf(
                    ScheduleItem.Title("Сегодня", Date()),
                    ScheduleItem.Subject(
                        id = 0,
                        index = "1",
                        title = "Предмет",
                        subtitle = "Подзаголовок",
                        type = SubjectType.Lecture,
                        note = null,
                        timeRange = TimeRange(Date(), Date(System.currentTimeMillis() + 60000*30)),
                    )
                ),
                todayItemIndex = -1,
                loadingState = UiLoadingState.Ready,
                onSubjectClicked = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
