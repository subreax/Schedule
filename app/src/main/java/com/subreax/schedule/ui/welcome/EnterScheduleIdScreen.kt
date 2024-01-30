package com.subreax.schedule.ui.welcome

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.subreax.schedule.R
import com.subreax.schedule.ui.component.LoadingButton
import kotlinx.coroutines.job

@Composable
fun EnterScheduleIdScreen(
    goBack: () -> Unit,
    goAhead: () -> Unit,
    viewModel: EnterScheduleIdViewModel = hiltViewModel()
) {
    val searchId by viewModel.searchId.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val errorText = viewModel.errorText
    val isError = errorText != null
    val isLoading = viewModel.isLoading
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            IconButton(onClick = goBack, modifier = Modifier.padding(start = 4.dp, top = 8.dp)) {
                Icon(Icons.Filled.ArrowBack, "", tint = MaterialTheme.colorScheme.outline)
            }
            Text(
                text = stringResource(R.string.initial_setup),
                modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp),
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                stringResource(R.string.enter_your_schedule_id),
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            )

            OutlinedTextField(
                value = searchId,
                onValueChange = viewModel::updateScheduleId,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .focusRequester(focusRequester),
                label = { Text(stringResource(R.string.identifier)) },
                isError = isError,
                supportingText = {
                    if (errorText != null) {
                        Text(errorText.toString(LocalContext.current))
                    }
                },
                singleLine = true
            )

            ScheduleIdHints(
                hints = suggestions,
                onClick = {
                    viewModel.updateScheduleId(it)
                    viewModel.submit()
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

        }

        LoadingButton(
            text = stringResource(R.string.submit),
            isLoading = isLoading,
            onClick = viewModel::submit,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )

    }

    LaunchedEffect(Unit) {
        this.coroutineContext.job.invokeOnCompletion {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navHomeEvent.collect {
            goAhead()
        }
    }
}

@Composable
fun ScheduleIdHints(
    hints: List<String>,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(hints) {
            HintItem(
                value = it,
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }
    }
}

@Composable
fun HintItem(
    value: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .clickable { onClick(value) }
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Filled.Search, contentDescription = "")

        Text(
            text = value,
            modifier = Modifier.weight(1f)
        )
    }
}
