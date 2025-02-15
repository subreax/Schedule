package com.subreax.schedule.ui.component.dialog.base

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.subreax.schedule.ui.theme.ScheduleTheme


@Composable
fun AppBaseDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    onFocusManager: (FocusManager) -> Unit = {},
    cancelButton: @Composable () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp
        ) {
            Column(Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)) {
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleMedium) {
                    title()
                }

                Spacer(Modifier.height(16.dp))

                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    content()
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    cancelButton()
                    confirmButton()
                }
            }
        }

        val focusManager = LocalFocusManager.current
        LaunchedEffect(focusManager) {
            onFocusManager(focusManager)
        }
    }
}

@PreviewLightDark
@Composable
private fun AppBaseDialogPreview() {
    ScheduleTheme {
        Surface(Modifier.fillMaxSize()) {
            AppBaseDialog(
                onDismissRequest = {},
                title = {
                    Text("Заголовок")
                },
                content = {
                    Text("Курс доллара упал со 110 до 90 рублей за 2 недели ура")
                },
                cancelButton = {
                    TextButton(onClick = {}) {
                        Text("Отмена")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {}) {
                        Text("Сохранить")
                    }
                }
            )
        }
    }
}