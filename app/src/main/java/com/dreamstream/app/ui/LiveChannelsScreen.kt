package com.dreamstream.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dreamstream.app.data.Category
import com.dreamstream.app.data.LiveStream

@Composable
fun LiveChannelsScreen(
    viewModel: MainViewModel,
    onChannelClick: (LiveStream) -> Unit,
    onBack: () -> Unit
) {
    val categories by viewModel.liveCategories.collectAsState()
    val streams by viewModel.liveStreams.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    LaunchedEffect(Unit) {
        if (categories.isEmpty()) viewModel.loadLiveCategories()
        viewModel.loadLiveStreams(null)
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
                text = selectedCategory?.category_name ?: "All Live Channels",
                color = Color.White,
                fontSize = 20.sp
            )
        }

        // Category chips
        if (categories.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = {
                            selectedCategory = null
                            viewModel.loadLiveStreams(null)
                        },
                        label = { Text("All") }
                    )
                    Spacer(Modifier.width(8.dp))
                }
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory?.category_id == cat.category_id,
                        onClick = {
                            selectedCategory = cat
                            viewModel.loadLiveStreams(cat.category_id)
                        },
                        label = { Text(cat.category_name) }
                    )
                    Spacer(Modifier.width(8.dp))
                }
            }
        }

        if (uiState is UiState.Loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF00E5FF))
            }
        } else {
            LazyColumn {
                items(streams) { stream ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .clickable { onChannelClick(stream) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF121218))
                    ) {
                        Text(
                            text = stream.name,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
