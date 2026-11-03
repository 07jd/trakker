package com.jd.trakker.data.remote.tmdb

import com.jd.trakker.BuildConfig
import com.jd.trakker.R
import com.jd.trakker.data.remote.ExceptionAPI
import com.jd.trakker.data.local.show.Show
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

enum class FetchType {
    Feed,
    Search
}

class ShowFetcher {
    private val api: TMDB by lazy {
        Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
            .create(TMDB::class.java)
    }

    private fun parseShows(data: TmdbResponse): List<Show> {
        return data.results.map {
            Show(
                id = it.id,
                genres = it.genres,
                overview = it.overview,
                vote_average = it.vote_average,
                backdrop_path = it.backdrop_path,
                poster_path = it.poster_path,
                adult = it.adult,
                name = it.name!!,
                first_air_date = it.first_air_date!!
            )
        }
    }

    private suspend fun fetch(name: String?, page: Int = 1, type: FetchType, adult: Boolean): List<Show> {
        var statusCode: Int?
        var body: TmdbResponse?

        // Make request
        try {
            val rawData = when (type) {
                FetchType.Feed -> api.fetchFeed(
                    TmdbRequestBody(
                        type = "show",
                        query = null,
                        page = page,
                        adult = adult
                    )
                )

                FetchType.Search -> api.fetchMedia(
                    TmdbRequestBody(
                        type = "show", query = name, page = page, adult = adult
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
            return parseShows(body)
        } catch (e: Exception) {
            println("Failed to parse shows: ${e.message}")
            throw ExceptionAPI.fromCode(0, R.string.error_data_parsing)
        }
    }

    suspend fun search(name: String, page: Int = 1, adult: Boolean = false): List<Show> {
        return fetch(name, page, FetchType.Search, adult)
    }

    suspend fun getFeed(page: Int = 1, adult: Boolean = false): List<Show> {
        return fetch(null, page, FetchType.Feed, adult)
    }
}