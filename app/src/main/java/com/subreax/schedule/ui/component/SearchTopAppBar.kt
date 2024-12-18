package com.subreax.schedule.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.R
import com.subreax.schedule.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    search: String,
    onSearchChanged: (String) -> Unit,
    hint: String,
    navBack: () -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    TopAppBar(
        title = {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                STextField(
                    value = search,
                    onValueChange = onSearchChanged,
                    hint = hint,
                    trailingIcon = {
                        if (search.isNotEmpty()) {
                            IconButton(onClick = { onSearchChanged("") }) {
                                Icon(Icons.Filled.Close, stringResource(R.string.clear))
                            }
                        }
                    },
                    keyboardOptions = keyboardOptions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = navBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.go_back))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        ),
        modifier = modifier
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SearchTopAppBarPreview() {
    ScheduleTheme {
        SearchTopAppBar(
            search = "",
            onSearchChanged = {},
            hint = "Подсказка",
            navBack = { }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SearchTopAppBarWithTextPreview() {
    ScheduleTheme {
        SearchTopAppBar(
            search = "Текст поиска",
            onSearchChanged = {},
            hint = "Подсказка",
            navBack = { }
        )
    }
}