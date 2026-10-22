package com.jd.trakker.data.remote.tmdb

import com.jd.trakker.BuildConfig
import com.jd.trakker.data.remote.ExceptionAPI
import com.jd.trakker.data.local.genre.MovieGenre
import com.jd.trakker.data.local.genre.ShowGenre
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GenreFetcher {
    private val api: TMDB by lazy {
        Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
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