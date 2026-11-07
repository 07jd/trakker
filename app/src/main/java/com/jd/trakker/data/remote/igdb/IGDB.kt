package com.jd.trakker.data.remote.igdb

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Request body
data class RequestBody(
    val page: Int?,
    val name: String?,
    val id: Int?
)

// API responses
@Suppress("PropertyName")
data class Cover(
    val id: Int,
    val image_id: String
)

data class ImageType(
    val id: Int,
    val name: String?
)

@Suppress("PropertyName")
data class Artwork(
    val id: Int,
    val image_id: String,
    val image_type: ImageType?
)

data class Platform(
    val id: Int,
    val abbreviation: String
)

data class Genre(
    val id: Int,
    val name: String,
)

@Suppress("PropertyName")
data class GameResponse(
    val id: Int,
    val name: String,
    val artworks: List<Artwork>?,
    val cover: Cover?,
    val first_release_date: Long,
    val platforms: List<Platform?>?,
    val rating: Double,
    val genres: List<Genre>?,
    val storyline: String?,
    val url: String
)

interface Api {
    @POST("api/igdb/feed")
    suspend fun fetchFeed(
        @Body request: RequestBody
    ): Response<List<GameResponse>>

    @POST("api/igdb/search")
    suspend fun fetchGames(
        @Body request: RequestBody
    ): Response<List<GameResponse>>
}