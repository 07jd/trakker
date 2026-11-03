package com.jd.trakker.data.remote.tmdb

import com.jd.trakker.BuildConfig
import com.jd.trakker.R
import com.jd.trakker.data.remote.ExceptionAPI
import com.jd.trakker.data.local.movie.Movie
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieFetcher {
    private val api: TMDB by lazy {
        Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
            .create(TMDB::class.java)
    }

    private fun parseMovies(data: TmdbResponse): List<Movie> {
        return data.results.map {
            println(it)
            Movie(
                id = it.id,
                adult = it.adult,
                title = it.title!!,
                genres = it.genres,
                overview = it.overview,
                release_date = it.release_date!!,
                vote_average = it.vote_average,
                video = it.video!!,
                backdrop_path = it.backdrop_path,
                poster_path = it.poster_path
            )
        }
    }

    private suspend fun fetch(name: String?, page: Int = 1, type: FetchType, adult: Boolean): List<Movie> {
        var statusCode: Int?
        var body: TmdbResponse?

        // Make request
        try {
            val rawData = when (type) {
                FetchType.Feed -> api.fetchFeed(
                    TmdbRequestBody(
                        type = "movie",
                        query = null,
                        page = page,
                        adult = adult
                    )
                )
                FetchType.Search -> api.fetchMedia(
                    TmdbRequestBody(
                        type = "movie", query = name, page = page
                    )
                )
            }

            statusCode = rawData.code()
            body = rawData.body()
        } catch (e: Exception) {
            // Fetch failed
            throw ExceptionAPI.fromException(e)
        }

        // Non 200 code
        if (statusCode != 200) throw ExceptionAPI.fromCode(statusCode)

        // Invalid response
        if (body == null) throw ExceptionAPI.fromCode(0, R.string.error_invalid_data)

        try {
            return parseMovies(body)
        } catch (e: Exception) {
            println("Failed to parse movies: ${e.message}")
            throw ExceptionAPI.fromCode(0, R.string.error_data_parsing)
        }
    }

    suspend fun search(name: String, page: Int = 1, adult: Boolean = false): List<Movie> {
        return fetch(name, page, FetchType.Search, adult)
    }

    suspend fun getFeed(page: Int = 1, adult: Boolean = false): List<Movie> {
        return fetch(null, page, FetchType.Feed, adult)
    }
}