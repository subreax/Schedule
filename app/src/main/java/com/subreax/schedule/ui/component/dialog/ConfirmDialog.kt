package com.subreax.schedule.ui.component.dialog

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.stringResource
import com.subreax.schedule.R
import com.subreax.schedule.ui.component.dialog.base.AppBaseDialog

@Composable
fun ConfirmDialog(
    title: String,
    content: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    confirmButtonText: String = stringResource(R.string.confirm),
    cancelButtonText: String = stringResource(R.string.cancel),
    onFocusManager: (FocusManager) -> Unit = {}
) {
    AppBaseDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        content = {
            Text(content)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmButtonText)
            }
        },
        cancelButton = {
            TextButton(onClick = onDismissRequest) {
                Text(cancelButtonText)
            }
        },
        onFocusManager = onFocusManager
    )
}
