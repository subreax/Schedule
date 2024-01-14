package com.subreax.schedule.ui.scheduleownermgr.ownerpicker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.component.SearchTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOPickerTopAppBar(
    search: String,
    onSearchChanged: (String) -> Unit,
    navBack: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    TopAppBar(
        title = {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                SearchTextField(
                    value = search,
                    onValueChange = onSearchChanged,
                    hint = "Введите идентификатор",
                    trailingIcon = {
                        IconButton(onClick = { onSearchChanged("") }) {
                            Icon(Icons.Filled.Close, "")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = navBack) {
                Icon(Icons.Filled.ArrowBack, "nav back")
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                2.dp
            )
        ),
        modifier = modifier
    )
}
