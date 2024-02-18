package com.subreax.schedule.ui.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.theme.ScheduleTheme


@Composable
fun ListPopupButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), shape)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.secondary,
            LocalTextStyle provides MaterialTheme.typography.labelLarge
        ) {
            content()
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ListPopupIconButtonPreview() {
    ScheduleTheme {
        Surface {
            ListPopupButton(
                onClick = { },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Вернуться")
                Icon(Icons.Filled.ExpandLess, "")
            }
        }
    }
}