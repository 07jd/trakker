package com.jd.trakker.Api.TMDB.Client

import com.jd.trakker.Api.ExceptionAPI
import com.jd.trakker.Api.TMDB.DB.MovieGenre
import com.jd.trakker.Api.TMDB.DB.ShowGenre
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GenreFetcher {
    private val url = "https://trakker.07jd.workers.dev/"
    private val api: TMDB by lazy {
        Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build()
            .create(TMDB::class.java)
    }

    suspend fun getGenres(): Pair<List<MovieGenre>, List<ShowGenre>> {
        var statusCode: Int?
        var body: TmdbGenresResponse?

        // Make request
        try {
            val rawData = api.fetchGenres()
            statusCode = rawData.code()
            body = rawData.body()
        } catch (e: Exception) {
            // Fetch failed
            throw ExceptionAPI.fromException(e)
        }

        // Non 200 code
        if (statusCode != 200) throw ExceptionAPI.fromCode(statusCode)

        // Invalid response
        if (body == null) throw ExceptionAPI.fromCode(0, "Invalid data received")

        try {
            return Pair(body.movies, body.tv)
        } catch (e: Exception) {
            println("Failed to parse movies: ${e.message}")
            throw ExceptionAPI.fromCode(0, "Failed to parse data")
        }
    }
}