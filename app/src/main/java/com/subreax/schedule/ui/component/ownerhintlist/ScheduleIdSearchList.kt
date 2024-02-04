package com.subreax.schedule.ui.component.ownerhintlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.component.LoadingContainer

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
                    SearchItem(
                        value = it,
                        onClick = onClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchItem(
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