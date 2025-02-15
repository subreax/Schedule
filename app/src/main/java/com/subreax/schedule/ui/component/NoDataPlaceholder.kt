package com.subreax.schedule.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.theme.ScheduleTheme

private val AlignmentCenterOneThird = BiasAlignment(0f, -1f / 3f)
private val IconSize = 64.dp

@Composable
fun NoDataPlaceholder(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = AlignmentCenterOneThird
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.outline,
                LocalTextStyle provides MaterialTheme.typography.titleMedium
            ) {
                Icon(icon, text, modifier = Modifier.size(IconSize))
                Text(text, textAlign = TextAlign.Center)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun NoDataPlaceholderPreview() {
    ScheduleTheme {
        Surface {
            NoDataPlaceholder(
                icon = Icons.Filled.Close,
                text = "Нет данных",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}