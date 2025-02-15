package com.subreax.schedule.ui.component.schedule_id_search_list

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.R
import com.subreax.schedule.ui.component.LoadingContainer
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun ScheduleIdSearchList(
    scheduleId: String,
    hints: List<String>,
    onClick: (String) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    isItPossibleToContinueWithUnknownId: Boolean = false
) {
    val trScheduleId = remember(scheduleId) {
        scheduleId.trim()
    }

    val showContinueItem = remember(isLoading) {
        if (!isLoading && isItPossibleToContinueWithUnknownId) {
            trScheduleId.isNotEmpty() && !hints.contains(trScheduleId)
        } else {
            false
        }
    }

    LoadingContainer(
        isLoading = isLoading,
        modifier = modifier,
        loadingText = stringResource(R.string.searching)
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(hints) {
                ScheduleIdSearchItem(
                    id = it,
                    onClick = { onClick(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            if (showContinueItem) {
                item {
                    ScheduleIdSearchItem(
                        id = stringResource(R.string.continue_with_s, trScheduleId),
                        onClick = { onClick(trScheduleId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else if (!isLoading && trScheduleId.isNotEmpty() && hints.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_results),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScheduleIdSearchListPreview() {
    ScheduleTheme {
        Surface {
            ScheduleIdSearchList(
                scheduleId = "2204",
                hints = listOf("220431", "620221"),
                onClick = { },
                isLoading = false,
                modifier = Modifier.fillMaxWidth(),
                isItPossibleToContinueWithUnknownId = true
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Loading")
@Composable
private fun ScheduleIdSearchListLoadingPreview() {
    ScheduleTheme {
        Surface {
            ScheduleIdSearchList(
                scheduleId = "",
                hints = listOf(),
                onClick = { },
                isLoading = true,
                modifier = Modifier
                    .heightIn(200.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "No results")
@Composable
private fun ScheduleIdSearchListNoResultsPreview() {
    ScheduleTheme {
        Surface {
            ScheduleIdSearchList(
                scheduleId = "Б660231",
                hints = listOf(),
                onClick = { },
                isLoading = false,
                modifier = Modifier
                    .heightIn(200.dp)
                    .fillMaxWidth(),
                isItPossibleToContinueWithUnknownId = true
            )
        }
    }
}
