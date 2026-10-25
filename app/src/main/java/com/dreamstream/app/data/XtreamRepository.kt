package com.dreamstream.app.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class XtreamRepository(
    private var baseUrl: String,
    private var username: String,
    private var password: String
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private fun api(): XtreamApi {
        val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(XtreamApi::class.java)
    }

    suspend fun login(): AccountInfo = api().getAccountInfo(username, password)

    suspend fun getLiveCategories() = api().getLiveCategories(username, password)
    suspend fun getLiveStreams(categoryId: String? = null) =
        api().getLiveStreams(username, password, categoryId = categoryId)

    suspend fun getVodCategories() = api().getVodCategories(username, password)
    suspend fun getVodStreams(categoryId: String? = null) =
        api().getVodStreams(username, password, categoryId = categoryId)

    suspend fun getSeriesCategories() = api().getSeriesCategories(username, password)
    suspend fun getSeries(categoryId: String? = null) =
        api().getSeries(username, password, categoryId = categoryId)
    suspend fun getSeriesInfo(seriesId: Int) =
