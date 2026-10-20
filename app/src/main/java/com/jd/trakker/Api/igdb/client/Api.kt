package com.jd.trakker.Api.igdb.client

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class RequestBody(
    val page: Int?,
    val name: String?,
    val id: Int?
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