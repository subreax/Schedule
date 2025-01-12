package com.subreax.schedule.ui.component.schedule.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.subreax.schedule.utils.runOnEachMinute

@Composable
private fun VisibleWhenActive(
    item: ScheduleItem,
    timeInclusive: Boolean = false,
    block: @Composable (minutesLeft: Int) -> Unit
) {
    var minutesLeft by remember { mutableIntStateOf(item.getMinutesLeftOrZero(timeInclusive)) }
    var isActive by remember { mutableStateOf(item.isActive) }

    LaunchedEffect(item) {
        runOnEachMinute { loop ->
            minutesLeft = item.getMinutesLeftOrZero(timeInclusive)
            isActive = item.isActive
            if (item.isExpired) {
                loop.stop()
            }
        }
    }

    if (isActive) {
        block(minutesLeft)
    } else {
        Spacer(Modifier)
    }
}

@Composable
private fun BaseLabel(
    leadingIcon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            leadingIcon,
            "",
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(12.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ActiveLabel(
    item: ScheduleItem.ActiveLabel,
    modifier: Modifier = Modifier
) {
    VisibleWhenActive(item = item, timeInclusive = true) { minutesLeft ->
        BaseLabel(
            leadingIcon = Icons.Filled.HourglassBottom,
            text = "Осталось $minutesLeft мин.",
            modifier = modifier
        )
    }
}

@Composable
fun PendingLabel(
    item: ScheduleItem.PendingLabel,
    modifier: Modifier = Modifier
) {
    VisibleWhenActive(item = item, timeInclusive = true) { minutesLeft ->
        BaseLabel(
            leadingIcon = Icons.Filled.AccessTime,
            text = "До начала $minutesLeft мин.",
            modifier = modifier
        )
    }
}
