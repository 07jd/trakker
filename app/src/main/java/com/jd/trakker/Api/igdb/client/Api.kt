package com.jd.trakker.Api.igdb.client

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class RequestBody(
    val operation: String,
    val page: Int?,
    val name: String?,
    val id: Int?
)

interface Api {
    @POST("api/igdb")
    suspend fun fetch(
        @Body request: RequestBody
    ): Response<List<GameResponse>>
}