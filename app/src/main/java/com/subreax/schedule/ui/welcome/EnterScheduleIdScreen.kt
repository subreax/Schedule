package com.subreax.schedule.ui.welcome

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.subreax.schedule.R
import com.subreax.schedule.ui.component.LoadingIndicator
import com.subreax.schedule.ui.component.schedule_id_search_list.ScheduleIdSearchList
import com.subreax.schedule.ui.theme.ScheduleTheme
import com.subreax.schedule.utils.UiText

@Composable
fun EnterScheduleIdScreen(
    goBack: () -> Unit,
    goAhead: () -> Unit,
    viewModel: EnterScheduleIdViewModel = hiltViewModel()
) {
    val searchId by viewModel.searchId.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val error by viewModel.error.collectAsState()
    val areHintsLoading by viewModel.areHintsLoading.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    EnterScheduleIdScreen(
        searchId = searchId,
        onSearchIdChanged = {
            viewModel.updateSearchId(it)
        },
        hints = suggestions,
        isSubmitting = isSubmitting,
        areHintsLoading = areHintsLoading,
        error = error,
        goBack = goBack,
        onSubmit = {
            focusManager.clearFocus()
            viewModel.submit(it)
        },
        searchIdFocusRequester = focusRequester
    )

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(Unit) {
        viewModel.navHomeEvent.collect {
            goAhead()
        }
    }
}

@Composable
fun EnterScheduleIdScreen(
    searchId: String,
    onSearchIdChanged: (String) -> Unit,
    hints: List<String>,
    isSubmitting: Boolean,
    areHintsLoading: Boolean,
    error: UiText?,
    goBack: () -> Unit,
    onSubmit: (String) -> Unit,
    searchIdFocusRequester: FocusRequester = remember { FocusRequester() },
) {
    Surface {
        Box(modifier = Modifier.imePadding()) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .fillMaxSize()
            ) {
                IconButton(
                    onClick = goBack,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "", tint = MaterialTheme.colorScheme.outline)
                }
                Text(
                    text = stringResource(R.string.initial_setup),
                    modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    stringResource(R.string.enter_your_schedule_id),
                    modifier = Modifier.padding(
                        top = 8.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                )

                OutlinedTextField(
                    value = searchId,
                    onValueChange = onSearchIdChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                        .focusRequester(searchIdFocusRequester),
                    label = {
                        Text(stringResource(R.string.identifier))
                    },
                    isError = error != null,
                    supportingText = {
                        if (error != null) {
                            Text(error.toString(LocalContext.current))
                        }
                    },
                    singleLine = true
                )

                ScheduleIdSearchList(
                    hints = hints,
                    onClick = onSubmit,
                    isLoading = areHintsLoading,
                    isSearchIdEmpty = searchId.isEmpty(),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }

            LoadingIndicator(
                isLoading = isSubmitting,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                    .fillMaxSize(),
                loadingText = "Загрузка"
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun EnterScheduleIdScreenPreview() {
    ScheduleTheme {
        EnterScheduleIdScreen(
            searchId = "2204",
            onSearchIdChanged = { },
            hints = listOf(),
            isSubmitting = false,
            areHintsLoading = false,
            error = null,
            goBack = { },
            onSubmit = { }
        )
    }
}
