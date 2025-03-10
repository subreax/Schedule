package com.subreax.schedule.ui.component.ac_schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.subreax.schedule.R
import com.subreax.schedule.ui.theme.ScheduleTheme
import com.subreax.schedule.ui.theme.scheduleColors


@Composable
fun PendingAcademicScheduleItem(
    title: String,
    begin: String,
    end: String,
    daysBeforeStart: Int,
    modifier: Modifier = Modifier
) {
    BaseAcademicScheduleItem(title = title, begin = begin, end = end, modifier = modifier) {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = pluralStringResource(
                    id = R.plurals.days,
                    count = daysBeforeStart,
                    daysBeforeStart
                ),
                color = MaterialTheme.scheduleColors.warning,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.before_start),
                color = MaterialTheme.scheduleColors.warning,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ActiveAcademicScheduleItem(
    title: String,
    begin: String,
    end: String,
    daysRemaining: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    BaseAcademicScheduleItem(title = title, begin = begin, end = end, modifier = modifier) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(52.dp)) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                strokeCap = StrokeCap.Butt,
                gapSize = 0.dp
            )

            Text(
                text = stringResource(R.string.days_short, daysRemaining),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun FinishedAcademicScheduleItem(
    title: String,
    begin: String,
    end: String,
    modifier: Modifier = Modifier
) {
    BaseAcademicScheduleItem(title = title, begin = begin, end = end, modifier = modifier) {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(R.string.finished),
                color = MaterialTheme.scheduleColors.success,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun BaseAcademicScheduleItem(
    title: String,
    begin: String,
    end: String,
    modifier: Modifier = Modifier,
    right: @Composable () -> Unit
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title)
            Text(
                text = "$begin - $end",
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        right()
    }
}


@PreviewLightDark
@Composable
private fun PendingAcademicScheduleItemPreview() {
    ScheduleTheme {
        Surface {
            PendingAcademicScheduleItem(
                title = "ликвидация задолженности",
                begin = "01.09.2024",
                end = "27.10.2024",
                daysBeforeStart = 35,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ActiveAcademicScheduleItemPreview() {
    ScheduleTheme {
        Surface {
            ActiveAcademicScheduleItem(
                title = "ликвидация задолженности",
                begin = "01.09.2024",
                end = "27.10.2024",
                daysRemaining = 35,
                progress = 0.7f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun FinishedAcademicScheduleItemPreview() {
    ScheduleTheme {
        Surface {
            FinishedAcademicScheduleItem(
                title = "ликвидация задолженности",
                begin = "01.09.2024",
                end = "27.10.2024",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}