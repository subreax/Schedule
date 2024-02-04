package com.subreax.schedule.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.subreax.schedule.R

@Composable
fun LoadingContainer(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    loadingText: String = stringResource(R.string.loading),
    onLoading: @Composable BoxScope.() -> Unit = {
        LoadingIndicator(
            loadingText = loadingText,
            modifier = Modifier.align(Alignment.Center)
        )
    },
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier) {
        if (isLoading) {
            onLoading()
        } else {
            content()
        }
    }
}
