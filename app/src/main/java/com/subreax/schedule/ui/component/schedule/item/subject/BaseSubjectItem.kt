package com.subreax.schedule.ui.component.schedule.item.subject

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun BaseSubjectItem(
    index: @Composable () -> Unit,
    typeIndicator: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    isActive: Boolean,
    highlightColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgModifier = remember(isActive, highlightColor) {
        if (isActive) {
            Modifier.background(
                Brush.linearGradient(
                    listOf(highlightColor, Color.Transparent)
                )
            )
        } else {
            Modifier
        }
    }

    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(bgModifier)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        index()
        typeIndicator()

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            content()
        }
    }
}
