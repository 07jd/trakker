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
data class Cover(
    val id: Int,
    val image_id: String
)

data class ImageType(
    val id: Int,
    val image_type: String
)

data class Artwork(
    val id: Int,
    val image_id: String,
    val imageType: ImageType
)

data class Platform(
    val id: Int,
    val abbreviation: String
)

data class ShallowGameResponse(
    val id: Int,
    val cover: Cover?,
    val name: String
)

data class GameResponse(
    val id: Int,
    val artworks: List<Artwork>?,
    val cover: Cover?,
    val first_release_date: Int,
    val name: String,
    val platforms: List<Platform>?,
    val rating: Double,
    val similar_games: List<ShallowGameResponse>?,
    val storyline: String,
    val summary: String,
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