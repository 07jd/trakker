package com.jd.trakker.data.remote.igdb

import com.jd.trakker.BuildConfig
import com.jd.trakker.R
import com.jd.trakker.data.remote.ExceptionAPI
import com.jd.trakker.data.local.item.Game
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameFetcher {
    private val api: Api by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    private fun parseGames(data: List<GameResponse?>?): List<Game> {
        if (data == null) return emptyList()

        val games = data.filterNotNull().map { game ->
            Game(
                id = game.id,
                url = game.url,
                name = game.name,
                rating = (game.rating / 10.0),
                summary = game.storyline ?: "",
                backdrops = game.artworks?.filter {
                    val type = it.image_type?.name

                    (   type == "Artwork"       ||
                        type == "Infographic"   ||
                        type == "Screenshot"    ||
                        type == "Concept art"
                    )
                }?.map { it.image_id } ?: emptyList(),
                genres = game.genres?.map {
                    // Remove abbreviations from name ex.: Input = Roleplay... (RPG) -> Roleplay...
                    it.name.replace(Regex("""\s*\(.*?\)"""), "").trim()
                }?.distinct() ?: emptyList(),
                releaseDate = game.first_release_date,
                cover = game.cover?.image_id,
                platforms = game.platforms?.mapNotNull { it?.abbreviation } ?: emptyList()
            )
        }

        return games
    }

    private suspend fun fetch(name: String?, id: Int?, page: Int?): List<Game> {
        var statusCode: Int?
        var body: List<GameResponse>?

        try {
            val rawData = when((name == null && id == null)) {
                true  -> api.fetchFeed(RequestBody(page, null, null))
                false -> api.fetchGames(RequestBody(page = page, name = name, id = id))
            }

            statusCode = rawData.code()
            body       = rawData.body()
        } catch (e: Exception) {
            throw ExceptionAPI.fromException(e)
        }

        // Verify data
        if(statusCode != 200) throw ExceptionAPI.fromCode(statusCode)
        if(body == null) throw ExceptionAPI.fromCode(0, R.string.error_invalid_data)

        try {
            return parseGames(body)
        } catch (e: Exception) {
            println("Failed to parse games: ${e.message}")
            throw ExceptionAPI.fromCode(0, R.string.error_data_parsing)
        }
    }

    suspend fun search(name: String, page: Int = 1): List<Game> {
        return fetch(name, null, page)
    }

    suspend fun getFeed(page: Int): List<Game> {
        return fetch(null, null, page)
    }
}