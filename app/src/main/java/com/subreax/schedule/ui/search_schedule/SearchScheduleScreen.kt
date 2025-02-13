package com.subreax.schedule.ui.search_schedule

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.subreax.schedule.ui.component.SearchScheduleIdScreen
import kotlinx.coroutines.isActive
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScheduleScreen(
    navBack: () -> Unit,
    navToScheduleExplorer: (String) -> Unit,
    viewModel: SearchScheduleViewModel = koinViewModel()
) {
    val searchId by viewModel.searchId.collectAsState()
    val ids by viewModel.ids.collectAsState()
    val isLoading by viewModel.isHintsLoading.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Scaffold(
        contentWindowInsets = WindowInsets.ime.union(WindowInsets.navigationBars),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        SearchScheduleIdScreen(
            ids = ids,
            searchId = searchId,
            onSearchIdChanged = viewModel::updateSearchId,
            onIdClicked = { id ->
                focusManager.clearFocus()
                navToScheduleExplorer(id)
            },
            isHintsLoading = isLoading,
            isSubmitting = false,
            navBack = navBack,
            modifier = Modifier.padding(it),
            focusRequester = focusRequester
        )
    }

    LaunchedEffect(focusRequester) {
        runCatching { focusRequester.requestFocus() }

        while (isActive) {
            val error = viewModel.errors.receive()
            snackbarHostState.showSnackbar(error.toString(context))
        }
    }
}
