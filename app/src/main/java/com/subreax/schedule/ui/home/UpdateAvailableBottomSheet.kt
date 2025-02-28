package com.subreax.schedule.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.subreax.schedule.BuildConfig
import com.subreax.schedule.R
import com.subreax.schedule.data.model.AppUpdateInfo
import com.subreax.schedule.ui.theme.ScheduleTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateAvailableBottomSheet(
    updateInfo: AppUpdateInfo,
    onDismissRequest: () -> Unit,
    navToDownload: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Box {
            UpdateAvailableSheetContent(
                updateInfo = updateInfo,
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(16.dp)
                    .fillMaxWidth(),
                bottomContentPadding = 92.dp
            )

            BottomContainer { paddingValues ->
                val fadeHeight = 48.dp
                val fadeHeightPx = with(LocalDensity.current) { fadeHeight.toPx() }

                Buttons(
                    onSubmit = {
                        navToDownload(updateInfo.downloadLink)
                    },
                    onDismiss = onDismissRequest,
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.surface
                                ), endY = fadeHeightPx
                            )
                        )
                        .padding(paddingValues)
                        .padding(
                            top = 16.dp + fadeHeight,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun UpdateAvailableSheetContent(
    updateInfo: AppUpdateInfo,
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp
) {
    Column(modifier) {
        Image(
            painter = painterResource(id = R.drawable.app_icon_round),
            contentDescription = "App icon",
            modifier = Modifier
                .padding(bottom = 16.dp)
                .size(72.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            stringResource(R.string.update_available),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = updateInfo.version,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = stringResource(R.string.whats_new),
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
            style = MaterialTheme.typography.labelLarge
        )

        Text(
            text = updateInfo.changes,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = bottomContentPadding)
        )
    }
}

@Composable
private fun Buttons(
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.hide))
        }

        Button(
            onClick = onSubmit,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.download))
        }
    }
}

@Composable
private fun BottomContainer(
    hideThreshold: Dp = 48.dp,
    content: @Composable (PaddingValues) -> Unit
) {
    val navigationBarsBottomPadding = WindowInsets.navigationBars
        .only(WindowInsetsSides.Bottom)
        .asPaddingValues()

    var screenHeight by remember { mutableIntStateOf(0) }

    Layout(content = {
        content(navigationBarsBottomPadding)

        val view = LocalView.current
        LaunchedEffect(view) {
            screenHeight = view.height
        }
    }) { measurables, constraints ->
        val placeable = measurables.first().measure(constraints)
        val height = placeable.height
        val hideThresholdPx = hideThreshold.toPx()

        layout(placeable.width, height) {
            coordinates?.let {
                val screenHeightF = screenHeight.toFloat()
                var y = it.screenToLocal(Offset(0f, screenHeightF - height)).y - hideThresholdPx

                if (y < 0) {
                    y *= -0.5f
                }

                placeable.place(0, y.roundToInt() + hideThresholdPx.toInt())
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun UpdateAvailableSheetContentPreview() {
    ScheduleTheme {
        Surface {
            UpdateAvailableSheetContent(
                updateInfo = AppUpdateInfo(
                    BuildConfig.VERSION_NAME,
                    "Огромные изменения просто огромные",
                    "",
                    0
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
