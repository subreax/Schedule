package com.subreax.schedule.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun SChoiceChip(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
    enabled: Boolean = true,
    highlighted: Boolean = false
) {
    val containerColor = if (highlighted) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        Color.Unspecified
    }

    AssistChip(
        onClick = onClick,
        label = { Text(text) },
        modifier = modifier,
        enabled = enabled,
        shape = CircleShape,
        colors = AssistChipDefaults.assistChipColors(containerColor = containerColor)
    )
}

@PreviewLightDark
@Composable
private fun ChoiceChipPreview() {
    ScheduleTheme {
        Surface {
            SChoiceChip(
                text = "Choice",
                onClick = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HighlightedChoiceChipPreview() {
    ScheduleTheme {
        Surface {
            SChoiceChip(
                text = "Choice",
                onClick = { },
                modifier = Modifier.padding(16.dp),
                highlighted = true
            )
        }
    }
}