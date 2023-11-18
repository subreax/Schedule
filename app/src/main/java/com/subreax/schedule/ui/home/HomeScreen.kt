package com.subreax.schedule.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.subreax.schedule.ui.component.Subject

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel()) {
    HomeScreen(homeViewModel.schedule)
}

@Composable
fun HomeScreen(schedule: List<HomeViewModel.ScheduleItem>) {
    LazyColumn {
        items(schedule) { item ->
            when (item) {
                is HomeViewModel.ScheduleItem.Subject -> {
                    Subject(
                        name = item.name,
                        place = item.place,
                        timeRange = item.timeRange,
                        type = item.type,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                    )
                }

                is HomeViewModel.ScheduleItem.Title -> {
                    Title(
                        title = item.title,
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 32.dp,
                            bottom = 8.dp
                        )
                    )
                }
            }
        }
    }
}


@Composable
fun Title(title: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(0.15f)
                .height(2.dp)
                .background(MaterialTheme.colorScheme.onSurface)
        )
    }
}