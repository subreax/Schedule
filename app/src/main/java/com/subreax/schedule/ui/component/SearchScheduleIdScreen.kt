package com.subreax.schedule.ui.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.subreax.schedule.R
import com.subreax.schedule.ui.component.schedule_id_search_list.ScheduleIdSearchList
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun SearchScheduleIdScreen(
    ids: List<String>,
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
            SearchTopAppBar(
                search = searchId,
                onSearchChanged = onSearchIdChanged,
                hint = stringResource(R.string.enter_schedule_id),
                navBack = navBack,
                focusRequester = focusRequester
            )

            ScheduleIdSearchList(
                scheduleId = searchId,
                hints = ids,
                onClick = {
                    onIdClicked(it)
                },
                isLoading = isHintsLoading,
                modifier = Modifier.fillMaxSize()
            )
        }

        LoadingIndicator(
            isLoading = isSubmitting,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                .fillMaxSize()
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SearchScheduleIdScreenPreview() {
    ScheduleTheme {
        Surface {
            SearchScheduleIdScreen(
                ids = listOf("220431", "221131"),
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