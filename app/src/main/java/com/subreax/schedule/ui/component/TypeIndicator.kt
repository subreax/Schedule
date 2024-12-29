package com.subreax.schedule.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.theme.subjectColorFrom

@Composable
fun TypeIndicator(type: SubjectType, modifier: Modifier = Modifier, cornerRadius: Dp = 4.dp) {
    Spacer(
        modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.subjectColorFrom(type))
    )
}
