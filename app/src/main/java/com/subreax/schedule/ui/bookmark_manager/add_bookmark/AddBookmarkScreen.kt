package com.subreax.schedule.ui.bookmark_manager.add_bookmark

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.subreax.schedule.ui.component.LoadingIndicator
import com.subreax.schedule.ui.component.ownerhintlist.ScheduleIdSearchList
import com.subreax.schedule.ui.theme.ScheduleTheme
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
        contentWindowInsets = WindowInsets.ime.add(WindowInsets.navigationBars),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        AddBookmarkScreen(
            hints = hints,
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

@Composable
fun AddBookmarkScreen(
    hints: List<String>,
    searchId: String,
    onSearchIdChanged: (String) -> Unit,
    onIdClicked: (String) -> Unit,
    isHintsLoading: Boolean,
    isSubmitting: Boolean,
    navBack: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    Box(modifier) {
        Column(Modifier.fillMaxSize()) {
            AddBookmarkTopAppBar(
                search = searchId,
                onSearchChanged = onSearchIdChanged,
                navBack = navBack,
                focusRequester = focusRequester
            )

            ScheduleIdSearchList(
                hints = hints,
                onClick = {
                    onIdClicked(it)
                },
                isLoading = isHintsLoading,
                isSearchIdEmpty = searchId.isEmpty(),
                modifier = Modifier.fillMaxSize()
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

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun AddBookmarkScreenPreview() {
    ScheduleTheme {
        Surface {
            AddBookmarkScreen(
                hints = listOf("220431", "221131"),
                searchId = "123",
                onSearchIdChanged = { },
                onIdClicked = { },
                isHintsLoading = false,
                isSubmitting = false,
                navBack = { }
            )
        }
    }
}
