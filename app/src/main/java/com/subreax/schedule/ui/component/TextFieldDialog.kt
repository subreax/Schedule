package com.subreax.schedule.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.subreax.schedule.R
import com.subreax.schedule.ui.theme.ScheduleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

private const val DialogActionDelayMs: Long = 200

@Composable
fun TextFieldDialog(
    dialogTitle: String,
    value: String,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    label: String,
    placeholder: String = "",
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    hideKeyboardAndDelayActions: Boolean = false
) {
    val focusManagerHolder = remember { ObjectHolder<FocusManager>() }

    fun doAction(action: () -> Unit) {
        if (hideKeyboardAndDelayActions) {
            focusManagerHolder.value?.clearFocus()
            coroutineScope.launch {
                delay(DialogActionDelayMs)
                action()
            }
        } else {
            action()
        }
    }

    Dialog(onDismissRequest = { doAction(onDismiss) }) {
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }

        TextFieldDialogContent(
            dialogTitle = dialogTitle,
            value = value,
            onValueChange = onValueChange,
            onSave = { doAction(onSave) },
            onDismiss = { doAction(onDismiss) },
            label = label,
            focusRequester = focusRequester,
            placeholder = placeholder
        )

        LaunchedEffect(focusRequester) {
            focusRequester.requestFocus()
            focusManagerHolder.value = focusManager
        }
    }
}


@Composable
private fun TextFieldDialogContent(
    dialogTitle: String,
    value: String,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    focusRequester: FocusRequester = remember { FocusRequester() },
    placeholder: String = ""
) {
    var tfv by remember {
        mutableStateOf(
            TextFieldValue(
                text = value,
                selection = TextRange(0, value.length)
            )
        )
    }

    Card(modifier) {
        Column(Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)) {
            Text(text = dialogTitle, style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = tfv,
                onValueChange = {
                    tfv = it
                    onValueChange(it.text)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .focusRequester(focusRequester),
                label = {
                    Text(text = label)
                },
                placeholder = {
                    Text(text = placeholder, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            ) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }

                TextButton(onClick = { onSave() }) {
                    Text(text = stringResource(R.string.save))
                }
            }

        }
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TextFieldDialogPreview() {
    ScheduleTheme {
        TextFieldDialog(
            dialogTitle = "Dialog title",
            value = "value",
            onValueChange = {},
            onSave = {},
            onDismiss = {},
            label = "label"
        )
    }
}

private class ObjectHolder<T> {
    private var ref = WeakReference<T>(null)

    var value: T?
        get() = ref.get()
        set(value) {
            ref = WeakReference(value)
        }
}