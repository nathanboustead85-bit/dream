package com.dreamstream.app.data

data class UserInfo(
    val auth: Int? = null,
    val status: String? = null,
    val exp_date: String? = null,
    val is_trial: String? = null,
    val active_cons: String? = null,
    val max_connections: String? = null,
    val username: String? = null
)

data class ServerInfo(
    val url: String? = null,
    val port: String? = null,
    val https_port: String? = null,
    val server_protocol: String? = null
)

data class AccountInfo(
    val user_info: UserInfo? = null,
    val server_info: ServerInfo? = null
)

data class Category(
    val category_id: String,
    val category_name: String,
    val parent_id: Int = 0
)

data class LiveStream(
    val num: Int = 0,
    val name: String,
    val stream_type: String? = null,
    val stream_id: Int,
    val stream_icon: String? = null,
    val epg_channel_id: String? = null,
    val category_id: String? = null
)

data class VodStream(
    val num: Int = 0,
    val name: String,
    val stream_type: String? = null,
    val stream_id: Int,
    val stream_icon: String? = null,
    val rating: String? = null,
    val category_id: String? = null
)
data class Series(
    val num: Int = 0,
    val name: String,
    val series_id: Int,
    val cover: String? = null,
    val plot: String? = null,
    val cast: String? = null,
    val director: String? = null,
    val genre: String? = null,
    val releaseDate: String? = null,
    val rating: String? = null,
    val category_id: String? = null
)

data class Season(
    val air_date: String? = null,
    val episode_count: Int = 0,
    val id: Int = 0,
    val name: String? = null,
    val season_number: Int = 0
)

data class Episode(
    val id: String,
    val episode_num: Int = 0,
    val title: String? = null,
    val container_extension: String? = null,
    val info: EpisodeInfo? = null
)

data class EpisodeInfo(
    val movie_image: String? = null,
    val plot: String? = null,
    val duration_secs: Int = 0,
    val duration: String? = null
)

data class SeriesInfo(
    val seasons: List<Season>? = null,
    val info: Series? = null,
    val episodes: Map<String, List<Episode>>? = null
)
