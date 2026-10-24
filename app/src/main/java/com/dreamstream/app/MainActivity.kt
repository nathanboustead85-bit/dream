package com.dreamstream.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dreamstream.app.ui.*

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val uiState by viewModel.uiState.collectAsState()

            // Auto-navigate if already logged in
            LaunchedEffect(uiState) {
                if (uiState is UiState.LoggedIn) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                composable("home") {
                    HomeScreen(
                        viewModel = viewModel,
                        onLiveClick = { navController.navigate("live") },
                        onMoviesClick = { navController.navigate("movies") },
                        onLogout = {
                            viewModel.logout()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable("live") {
                    LiveChannelsScreen(
                        viewModel = viewModel,
                        onChannelClick = { stream ->
                            val url = viewModel.getLiveUrl(stream.stream_id)
                            if (url != null) {
                                navController.navigate("player/${stream.name}?url=$url")
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("movies") {
                    MoviesScreen(
                        viewModel = viewModel,
                        onMovieClick = { movie ->
                            val url = viewModel.getVodUrl(movie.stream_id)
                            if (url != null) {
                                navController.navigate("player/${movie.name}?url=$url")
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("player/{title}?url={url}") { backStackEntry ->
                    val title = backStackEntry.arguments?.getString("title") ?: "Playing"
                    val url = backStackEntry.arguments?.getString("url") ?: ""

                    PlayerScreen(
                        title = title,
                        streamUrl = url,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
