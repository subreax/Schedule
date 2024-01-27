package com.subreax.schedule.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.getArgbColor

@Composable
fun TypeIndicator(type: SubjectType, modifier: Modifier = Modifier, cornerRadius: Dp = 4.dp) {
    val color = remember(type) { Color(type.getArgbColor()) }

    Spacer(
        modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(color)
    )
}
