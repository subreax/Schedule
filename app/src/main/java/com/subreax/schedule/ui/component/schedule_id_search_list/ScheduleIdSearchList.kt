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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.component.LoadingContainer
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun ScheduleIdSearchList(
    hints: List<String>,
    onClick: (String) -> Unit,
    isLoading: Boolean,
    isSearchIdEmpty: Boolean,
    modifier: Modifier = Modifier
) {
    LoadingContainer(
        isLoading = isLoading,
        modifier = modifier,
        loadingText = "Поиск"
    ) {
        if (hints.isEmpty()) {
            if (!isSearchIdEmpty) {
                Text(
                    text = "Нет результатов",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
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
                hints = listOf("220431", "620221"),
                onClick = { },
                isLoading = false,
                isSearchIdEmpty = false,
                modifier = Modifier.fillMaxWidth()
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
                hints = listOf(),
                onClick = { },
                isLoading = true,
                isSearchIdEmpty = false,
                modifier = Modifier.heightIn(200.dp).fillMaxWidth()
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
                hints = listOf(),
                onClick = { },
                isLoading = false,
                isSearchIdEmpty = false,
                modifier = Modifier.heightIn(200.dp).fillMaxWidth()
            )
        }
    }
}
