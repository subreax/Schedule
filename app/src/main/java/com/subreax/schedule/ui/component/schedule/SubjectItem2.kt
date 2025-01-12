package com.subreax.schedule.ui.component.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.ui.component.schedule.item.subject.SubjectItem


private const val HOUR = 1000 * 60 * 60L


@Composable
fun SubjectItem2(
    item: ScheduleItem.Subject,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
    modifier: Modifier,
    indexModifier: Modifier,
) {
    var isActive by remember { mutableStateOf(item.isActive) }

    // Текущий и будущие предметы в пределах некоторого времени будут определять,
    // сколько минут до конца (и активны ли они)
    if (item.couldBeActive()) {
        LaunchedEffect(item) {
            runOnEachMinute { loop ->
                isActive = item.isActive
                if (item.isExpired) {
                    loop.stop()
                }
            }
        }
    }

    SubjectItem(
        index = item.index,
        title = item.title,
        subtitle = item.subtitle,
        type = item.type,
        note = item.note,
        onClick = { onSubjectClicked(item) },
        isActive = isActive,
        modifier = modifier,
        indexModifier = indexModifier
    )
}


private fun ScheduleItem.Subject.couldBeActive(): Boolean {
    val t = end.time - System.currentTimeMillis()
    return t >= 0 && t <= (8 * HOUR)
}
