package com.subreax.schedule.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Title(title: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(0.15f)
                .height(2.dp)
                .background(MaterialTheme.colorScheme.onSurface)
        )
    }
}
