package com.dreamstream.app.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun PlayerScreen(
    title: String,
    streamUrl: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(streamUrl))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                isLoading = state == Player.STATE_BUFFERING || state == Player.STATE_IDLE
                if (state == Player.STATE_READY) hasError = false
            }

            override fun onPlayerError(error: PlaybackException) {
                hasError = true
                isLoading = false
                errorMessage = error.message ?: "Playback error"
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading && !hasError) {
            CircularProgressIndicator(
                color = Color(0xFF00E5FF),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (hasError) {
            Column(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Playback Error", color = Color.White, fontSize = 20.sp)
                Spacer(Modifier.height(8.dp))
                Text(errorMessage, color = Color.Gray, fontSize = 14.sp)
                Spacer(Modifier.height(24.dp))
                TextButton(onClick = {
                    hasError = false
                    isLoading = true
                    exoPlayer.prepare()
                    exoPlayer.play()
                }) {
                    Text("Retry", color = Color(0xFF00E5FF))
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onBack) {
                    Text("Go Back", color = Color.White)
                }
            }
        }

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(12.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← Back", color = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Text(text = title, color = Color.White, maxLines = 1)
        }
    }
}
