package com.jd.trakker.data.remote.tmdb

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Body sent to the API
data class TmdbRequestBody(
    val type: String?,
    val query: String?,
    val page: Int?,
    val adult: Boolean = false,
)

// API raw responses
data class TmdbResponse(
    val results: List<TmdbDataResponse>
)

@Suppress("PropertyName")
data class TmdbDataResponse(
    val id: Int,
    val adult: Boolean,
    val genres: List<String>,

    // Title = Movie
    // Name  = Show
    val name: String?,
    val title: String?,

    // Only for movies, indicates if It's available to watch online or only on theaters
    val video: Boolean?,

    // Release date for movies
    // First air date for shows
    val first_air_date: String?,
    val release_date: String?,

    val overview: String,
    val vote_average: Double,
    val backdrop_path: String?,
    val poster_path: String?,
)

interface TMDB {
    @POST("/api/tmdb/feed")
    suspend fun fetchFeed(
        @Body requestBody: TmdbRequestBody
    ): Response<TmdbResponse>

    @POST("/api/tmdb/search")
    suspend fun fetchMedia(
        @Body requestBody: TmdbRequestBody
    ): Response<TmdbResponse>
}