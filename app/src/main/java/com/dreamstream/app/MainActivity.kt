package com.dreamstream.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dreamstream.app.ui.*
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val uiState by viewModel.uiState.collectAsState()

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
                        onSeriesClick = { navController.navigate("series") },
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
                                val encodedTitle = URLEncoder.encode(stream.name, StandardCharsets.UTF_8.toString())
                                val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                navController.navigate("player/$encodedTitle/$encodedUrl")
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
                                val encodedTitle = URLEncoder.encode(movie.name, StandardCharsets.UTF_8.toString())
                                val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                navController.navigate("player/$encodedTitle/$encodedUrl")
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("series") {
    SeriesScreen(
        viewModel = viewModel,
        onSeriesClick = { series ->
            // We pass the series_id and name via navigation
            val encodedName = URLEncoder.encode(series.name, StandardCharsets.UTF_8.toString())
            navController.navigate("series_detail/${series.series_id}/$encodedName")
        },
        onBack = { navController.popBackStack() }
    )
}

composable(
    route = "series_detail/{seriesId}/{name}",
    arguments = listOf(
        navArgument("seriesId") { type = NavType.IntType },
        navArgument("name") { type = NavType.StringType }
    )
) { backStackEntry ->
    val seriesId = backStackEntry.arguments?.getInt("seriesId") ?: 0
    val name = URLDecoder.decode(
        backStackEntry.arguments?.getString("name") ?: "Series",
        StandardCharsets.UTF_8.toString()
    )

    val series = Series(name = name, series_id = seriesId)

    SeriesDetailScreen(
        viewModel = viewModel,
        series = series,
        onEpisodeClick = { episode ->
            val url = viewModel.getSeriesEpisodeUrl(episode.id)
            if (url != null) {
                val encodedTitle = URLEncoder.encode(
                    episode.title ?: "Episode ${episode.episode_num}",
                    StandardCharsets.UTF_8.toString()
                )
                val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                navController.navigate("player/$encodedTitle/$encodedUrl")
            }
        },
        onBack = { navController.popBackStack() }
    )
}
                composable(
                    route = "player/{title}/{url}",
                    arguments = listOf(
                        navArgument("title") { type = NavType.StringType },
                        navArgument("url") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val title = URLDecoder.decode(
                        backStackEntry.arguments?.getString("title") ?: "Playing",
                        StandardCharsets.UTF_8.toString()
                    )
                    val url = URLDecoder.decode(
                        backStackEntry.arguments?.getString("url") ?: "",
                        StandardCharsets.UTF_8.toString()
                    )

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
