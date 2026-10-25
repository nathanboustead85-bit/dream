package com.dreamstream.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onLiveClick: () -> Unit,
    onMoviesClick: () -> Unit,
    onLogout: () -> Unit
) {
    val account by viewModel.accountInfo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050508))
            .padding(24.dp)
    ) {
        Text(
            text = "DreamStream Ultra",
            color = Color(0xFF00E5FF),
            fontSize = 24.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Status: ${account?.user_info?.status ?: "Unknown"}  •  Expires: ${account?.user_info?.exp_date ?: "—"}",
            color = Color.Gray,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(32.dp))

        MenuButton("Live TV", onLiveClick)
        Spacer(Modifier.height(12.dp))
        MenuButton("Movies", onMoviesClick)
        Spacer(Modifier.height(12.dp))
        MenuButton("Series (coming soon)") { }
        Spacer(Modifier.height(12.dp))
        MenuButton("Favourites (coming soon)") { }

        Spacer(Modifier.weight(1f))

        TextButton(onClick = onLogout) {
            Text("Logout", color = Color.Red)
        }
    }
}

@Composable
private fun MenuButton(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121218))
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Text(text, color = Color.White, fontSize = 18.sp)
        }
    }
}
