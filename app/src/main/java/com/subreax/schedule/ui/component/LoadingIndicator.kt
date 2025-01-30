package com.subreax.schedule.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.subreax.schedule.R
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun LoadingIndicator(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    loadingText: String = stringResource(R.string.loading)
) {
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .clickable(
                onClick = {},
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .then(modifier)
    ) {
        LoadingIndicator(loadingText = loadingText)
    }
}

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    loadingText: String = stringResource(R.string.loading)
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(text = loadingText, modifier = Modifier.padding(top = 16.dp))
    }
}

@PreviewLightDark
@Composable
fun LoadingIndicatorPreview() {
    ScheduleTheme {
        Surface {
            LoadingIndicator(modifier = Modifier.padding(32.dp))
        }
    }
}