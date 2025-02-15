package com.subreax.schedule.ui.component.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import com.subreax.schedule.R
import com.subreax.schedule.ui.component.dialog.base.AppBaseDialog

@Composable
fun TextInputDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    focusRequester: FocusRequester = remember { FocusRequester() },
    label: String = "",
    placeholder: String = "",
    confirmButtonText: String = stringResource(R.string.save),
    cancelButtonText: String = stringResource(R.string.cancel),
    onFocusManager: (FocusManager) -> Unit = {}
) {
    AppBaseDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        content = {
            var tfv by remember {
                mutableStateOf(
                    TextFieldValue(
                        text = value,
                        selection = TextRange(0, value.length)
                    )
                )
            }

            OutlinedTextField(
                value = tfv,
                onValueChange = {
                    tfv = it
                    onValueChange(it.text)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = {
                    Text(text = label)
                },
                placeholder = {
                    Text(text = placeholder, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                singleLine = true
            )
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