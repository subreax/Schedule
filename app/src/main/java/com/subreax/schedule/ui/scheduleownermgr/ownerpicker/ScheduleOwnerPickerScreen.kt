package com.subreax.schedule.ui.scheduleownermgr.ownerpicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ScheduleOwnerPickerScreen(
    navBack: () -> Unit,
    viewModel: ScheduleOwnerPickerViewModel = hiltViewModel()
) {
    val search by viewModel.searchId.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val focusRequester = remember { FocusRequester() }

    Column(Modifier.fillMaxSize()) {
        SOPickerTopAppBar(
            search = search,
            onSearchChanged = viewModel::updateSearchId,
            navBack = navBack,
            focusRequester = focusRequester
        )

        SuggestionsList(
            suggestions = suggestions,
            onSuggestionClicked = {
                viewModel.saveId(it)
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(Unit) {
        viewModel.navBackEvent.collect {
            navBack()
        }
    }
}


@Composable
private fun SuggestionsList(
    suggestions: List<String>,
    onSuggestionClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(suggestions) {
            SuggestionItem(
                value = it,
                onClick = { onSuggestionClicked(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun SuggestionItem(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(modifier)
    ) {
        Icon(Icons.Filled.Search, "")
        Text(text = value, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
