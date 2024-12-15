package com.subreax.schedule.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset

private const val AnimationDurationMs = 500

@Composable
fun AnimatedText(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedContentScope.(String) -> Unit
) {
    AnimatedContent(
        targetState = text,
        modifier = modifier,
        transitionSpec = {
            slideIn(
                animationSpec = tween(AnimationDurationMs),
                initialOffset = { IntOffset(0, it.height + 10) }
            ) togetherWith slideOut(
                animationSpec = tween(AnimationDurationMs),
                targetOffset = { IntOffset(0, -(it.height + 10)) }
            )
        },
        label = "AnimatedText"
    ) { text1 ->
        content(text1)
    }
}
