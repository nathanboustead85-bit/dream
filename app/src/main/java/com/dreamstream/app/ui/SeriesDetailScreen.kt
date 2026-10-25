package com.dreamstream.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dreamstream.app.data.Episode
import com.dreamstream.app.data.Series
import com.dreamstream.app.data.SeriesInfo

@Composable
fun SeriesDetailScreen(
    viewModel: MainViewModel,
    series: Series,
    onEpisodeClick: (Episode) -> Unit,
    onBack: () -> Unit
) {
    var seriesInfo by remember { mutableStateOf<SeriesInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedSeason by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(series.series_id) {
        isLoading = true
        error = null
        try {
            val info = viewModel.getSeriesInfo(series.series_id)
            seriesInfo = info
            // Select first season by default
            selectedSeason = info?.episodes?.keys?.firstOrNull()
        } catch (e: Exception) {
            error = e.message ?: "Failed to load series info"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050508))
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← Back", color = Color(0xFF00E5FF))
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = series.name,
                color = Color.White,
                fontSize = 18.sp,
                maxLines = 1
            )
        }

        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00E5FF))
                }
            }
            error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error ?: "", color = Color.Red)
                }
            }
            else -> {
                val episodesMap = seriesInfo?.episodes ?: emptyMap()
                val seasons = episodesMap.keys.sortedBy { it.toIntOrNull() ?: 0 }

                // Season chips
                if (seasons.isNotEmpty()) {
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        items(seasons) { seasonKey ->
                            FilterChip(
                                selected = selectedSeason == seasonKey,
                                onClick = { selectedSeason = seasonKey },
                                label = { Text("Season $seasonKey") }
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                    }
                }

                val currentEpisodes = selectedSeason?.let { episodesMap[it] } ?: emptyList()

                LazyColumn {
                    items(currentEpisodes) { episode ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .clickable { onEpisodeClick(episode) },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF121218))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Episode ${episode.episode_num}: ${episode.title ?: "Episode ${episode.episode_num}"}",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                if (!episode.info?.duration.isNullOrBlank()) {
                                    Text(
                                        text = episode.info?.duration ?: "",
                                        color = Color.Gray,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
