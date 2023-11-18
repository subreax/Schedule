package com.subreax.schedule.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.SubjectType

private val subjectColors = arrayOf(
    Color(0xFF148175), // lecture
    Color(0xFF2FAB1B), // practice
    Color(0xFFCF6B21), // lab
    Color(0xFFCA202E), // exam
    Color(0xFFFF00FF)  // unknown
)

@Composable
fun TypeIndicator(type: SubjectType, modifier: Modifier = Modifier) {
    val color = remember(type) { subjectColors[type.ordinal] }

    Spacer(
        modifier
            .size(4.dp, 24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
    )
}
