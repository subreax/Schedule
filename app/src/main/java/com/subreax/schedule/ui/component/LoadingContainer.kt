package com.subreax.schedule.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
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
    transitionSpec: AnimatedContentTransitionScope<Boolean>.() -> ContentTransform = {
        fadeIn().togetherWith(fadeOut())
    },
    content: @Composable BoxScope.() -> Unit,
) {
    LoadingContainer(
        isLoading = isLoading,
        modifier = modifier,
        transitionSpec = transitionSpec,
        onLoading = {
            LoadingIndicator(
                loadingText = loadingText,
                modifier = Modifier.align(Alignment.Center)
            )
        },
        content = content
    )
}

@Composable
fun LoadingContainer(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    transitionSpec: AnimatedContentTransitionScope<Boolean>.() -> ContentTransform = {
        fadeIn().togetherWith(fadeOut())
    },
    onLoading: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    AnimatedContent(
        targetState = isLoading,
        modifier = modifier,
        label = "LoadingContainer",
        transitionSpec = transitionSpec
    ) { isLoading1 ->
        Box(Modifier.fillMaxSize()) {
            if (isLoading1) {
                onLoading()
            } else {
                content()
            }
        }
    }
}
