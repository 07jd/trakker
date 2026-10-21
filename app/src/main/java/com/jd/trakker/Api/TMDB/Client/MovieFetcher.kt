package com.jd.trakker.Api.TMDB.Client

import com.jd.trakker.Api.ExceptionAPI
import com.jd.trakker.Api.TMDB.DB.Movie
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieFetcher {
    private val url = "https://trakker.07jd.workers.dev/"
    private val api: TMDB by lazy {
        Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build()
            .create(TMDB::class.java)
    }

    private fun parseMovies(data: TmdbResponse): List<Movie> {
        return data.results.map {
            Movie(
                id = it.id,
                adult = it.adult,
                title = it.title!!,
                genre_ids = it.genre_ids,
                overview = it.overview,
                release_date = it.release_date!!,
                vote_average = it.vote_average,
                video = it.video!!,
                backdrop_path = it.backdrop_path,
                poster_path = it.poster_path
            )
        }
    }

    private suspend fun fetch(name: String?, page: Int = 1, type: FetchType): List<Movie> {
        var statusCode: Int?
        var body: TmdbResponse?

        // Make request
        try {
            val rawData = when (type) {
                FetchType.Feed -> api.fetchFeed(
                    TmdbRequestBody(
                        type = "movie",
                        name = null,
                        page = page
                    )
                )

                FetchType.Search -> api.fetchMedia(
                    TmdbRequestBody(
                        type = "movie", name = name, page = page
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
        if (body == null) throw ExceptionAPI.fromCode(0, "Invalid data received")

        try {
            return parseMovies(body)
        } catch (e: Exception) {
            println("Failed to parse movies: ${e.message}")
            throw ExceptionAPI.fromCode(0, "Failed to parse data")
        }
    }

    suspend fun searchShow(name: String, page: Int = 1): List<Movie> {
        return fetch(name, page, FetchType.Search)
    }

    suspend fun getFeed(page: Int = 1): List<Movie> {
        return fetch(null, page, FetchType.Feed)
    }
}