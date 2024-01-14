package com.subreax.schedule.ui.scheduleownermgr

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun EditScheduleOwnerNameDialog(
    name: String,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        val focusRequester = remember { FocusRequester() }

        EditScheduleOwnerNameDialogContent(
            name = name,
            onNameChange = onNameChange,
            onSave = onSave,
            onDismiss = onDismiss,
            focusRequester = focusRequester
        )

        LaunchedEffect(focusRequester) {
            focusRequester.requestFocus()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScheduleOwnerNameDialogContent(
    name: String,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    var tfv by remember {
        mutableStateOf(
            TextFieldValue(
                text = name,
                selection = TextRange(0, name.length)
            )
        )
    }

    Card(modifier) {
        Column(Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)) {
            Text(text = "Изменение имени", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = tfv,
                onValueChange = {
                    tfv = it
                    onNameChange(it.text)
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .focusRequester(focusRequester),
                label = {
                    Text(text = "Имя")
                },
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Отмена")
                }

                TextButton(onClick = { onSave() }) {
                    Text(text = "Сохранить")
                }
            }

        }
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EditScheduleOwnerNameDialogPreview() {
    ScheduleTheme {
        EditScheduleOwnerNameDialogContent(
            name = "Name",
            onNameChange = {},
            onSave = {},
            onDismiss = {}
        )
    }
}