package com.subreax.schedule.ui.bookmark_manager.add_bookmark

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.subreax.schedule.ui.component.SearchScheduleIdScreen
import kotlinx.coroutines.isActive

@Composable
fun AddBookmarkScreen(
    navBack: () -> Unit,
    viewModel: AddBookmarkViewModel = hiltViewModel()
) {
    val searchId by viewModel.searchId.collectAsState()
    val hints by viewModel.hints.collectAsState()
    val isHintsLoading by viewModel.isHintsLoading.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Scaffold(
        contentWindowInsets = WindowInsets.ime.union(WindowInsets.navigationBars),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        SearchScheduleIdScreen(
            ids = hints,
            searchId = searchId,
            onSearchIdChanged = viewModel::updateSearchId,
            onIdClicked = { id ->
                focusManager.clearFocus()
                viewModel.addBookmark(id)
            },
            isHintsLoading = isHintsLoading,
            isSubmitting = isSubmitting,
            navBack = navBack,
            modifier = Modifier.padding(it),
            focusRequester = focusRequester
        )
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            val error = viewModel.errors.receive()
            snackbarHostState.showSnackbar(error.toString(context))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navBackEvent.collect {
            navBack()
        }
    }
}
