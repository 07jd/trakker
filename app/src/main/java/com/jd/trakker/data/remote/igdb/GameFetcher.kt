package com.jd.trakker.data.remote.igdb

import com.jd.trakker.BuildConfig
import com.jd.trakker.data.remote.ExceptionAPI
import com.jd.trakker.data.local.game.Game
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
                name = game.name,
                rating = game.rating,
                storyline = game.storyline,
                summary = game.summary,
                url = game.url,
                artworks = game.artworks?.filter { it.imageType.image_type == "Artwork" }?.map{ it.image_id } ?: emptyList(),
                cover = game.cover?.image_id ?: "NO_COVER_IMAGE",
                platforms = game.platforms?.map { it.abbreviation } ?: emptyList(),
                similarGames = game.similar_games?.map { it.id } ?: emptyList()
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
        if(body == null) throw ExceptionAPI.fromCode(0, "Invalid data received")

        try {
            return parseGames(body)
        } catch (e: Exception) {
            println("Failed to parse games: $body")
            throw ExceptionAPI.fromException(e)
        }
    }
    suspend fun searchGameByName(name: String, page: Int = 1): List<Game> {
        return fetch(name, null, page)
    }

    suspend fun searchGameByID(id: Int): Game? {
        return fetch(null, id, null).firstOrNull()
    }
    suspend fun getFeed(page: Int): List<Game> {
        return fetch(null, null, page)
    }
}