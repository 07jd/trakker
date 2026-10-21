package com.jd.trakker.api.tmdb.client

import com.jd.trakker.api.tmdb.db.MovieGenre
import com.jd.trakker.api.tmdb.db.ShowGenre
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Body sent to the API
data class TmdbRequestBody(
    val type: String?,
    val name: String?,
    val page: Int?
)

// API raw responses
data class TmdbResponse(
    val results: List<TmdbDataResponse>
)

data class TmdbDataResponse(
    val id: Int,
    val adult: Boolean,
    val genre_ids: List<Int>,

    // Title = Movie
    // Name  = Show
    val name: String?,
    val title: String?,

    // Only for movies, indicates if its available to watch online or only on theaters
    val video: Boolean?,

    // Release date for movies
    // First air date for shows
    val first_air_date: String?,
    val release_date: String?,

    val overview: String,
    val vote_average: Double,
    val backdrop_path: String,
    val poster_path: String,
)

data class TmdbGenresResponse(
    val movies: List<MovieGenre>,
    val tv: List<ShowGenre>
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

    @GET("/api/tmdb/genres")
    suspend fun fetchGenres(): Response<TmdbGenresResponse>
}