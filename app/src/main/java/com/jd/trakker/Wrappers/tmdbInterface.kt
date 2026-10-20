package com.jd.trakker.Wrappers

import retrofit2.http.GET

interface tmdbInterface {
    companion object {
        const val baseURL = "https://api.themoviedb.org/3/"
        const val imgURL = ""
    }

    @GET("authentication")
    suspend fun checkAuth() : AuthResponse
}