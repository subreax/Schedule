package com.subreax.schedule.ui.component.ac_schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.theme.ScheduleTheme

private val ItemModifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)

@Composable
fun AcademicScheduleList(
    schedule: List<UiAcademicScheduleItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        schedule.forEach {
            when (it) {
                is UiAcademicScheduleItem.Pending -> {
                    PendingAcademicScheduleItem(
                        title = it.title,
                        begin = it.begin,
                        end = it.end,
                        daysBeforeStart = it.daysBeforeStart,
                        modifier = ItemModifier
                    )
                }

                is UiAcademicScheduleItem.Active -> {
                    ActiveAcademicScheduleItem(
                        title = it.title,
                        begin = it.begin,
                        end = it.end,
                        daysRemaining = it.daysRemaining,
                        progress = it.progress,
                        modifier = ItemModifier
                    )
                }

                is UiAcademicScheduleItem.Finished -> {
                    FinishedAcademicScheduleItem(
                        title = it.title,
                        begin = it.begin,
                        end = it.end,
                        modifier = ItemModifier
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun AcademicSchedulePreview() {
    ScheduleTheme {
        Surface {
            AcademicScheduleList(
                schedule = listOf(
                    UiAcademicScheduleItem.Active("теоретич. обуч.", "01.09.24", "26.10.24", 0.6f, 20),
                    UiAcademicScheduleItem.Finished(
                        "ликвидация задолженности",
                        "23.09.24",
                        "20.10.24"
                    ),
                    UiAcademicScheduleItem.Pending("экзамен. сессия", "27.10.24", "02.11.24", 15),
                    UiAcademicScheduleItem.Pending("каникулы", "26.01.25", "08.02.25", 35)
                )
            )
        }
    }
}