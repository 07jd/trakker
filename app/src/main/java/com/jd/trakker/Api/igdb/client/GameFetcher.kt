package com.jd.trakker.Api.igdb.client

import com.jd.trakker.Api.igdb.db.Game
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameFetcher {
    private val url = "https://trakker.07jd.workers.dev/"
    private val api: Api by lazy {
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    private fun parseData(data: List<GameResponse?>? ): List<Game> {
        if (data == null) return emptyList()

        val games = data.filterNotNull().map { game ->
            Game(
                id = game.id,
                name = game.name,
                rating = game.rating,
                storyline = game.storyline,
                summary = game.summary,
                url = game.url,
                artworks = game.artworks?.map { it.image_id } ?: emptyList(),
                cover = game.cover?.image_id ?: "NO_COVER_IMAGE",
                platforms = game.platforms?.map { it.abbreviation } ?: emptyList(),
                similarGames = game.similar_games?.map { it.id } ?: emptyList()
            )
        }

        return games
    }

    private suspend fun searchGame(name: String?, id: Int?, page: Int?): List<Game> {
        val fetchResult = api.fetchGames( RequestBody (
                page = page,
                name = name,
                id = id
            )
        )

        val responseBody = fetchResult.body()
        if (!fetchResult.isSuccessful || responseBody == null) {
            println("API FETCH FAILED! Code: ${fetchResult.code()}, Message: ${fetchResult.message()}")
            return emptyList()
        }

        return parseData(responseBody)
    }

    suspend fun searchGameByName(name: String, page: Int = 1): List<Game> {
        return searchGame(name, null, page)
    }

    suspend fun searchGameByID(id: Int): List<Game> {
        return searchGame(null, id, null)
    }

    suspend fun getFeed(page: Int): List<Game> {
        val fetchResult = api.fetchFeed(RequestBody(
            page = page,
            id   = null,
            name = null
        ))

        val responseBody = fetchResult.body()
        if (!fetchResult.isSuccessful || responseBody == null) {
            println("API FETCH FAILED! Code: ${fetchResult.code()}, Message: ${fetchResult.message()}")
            return emptyList()
        }

        return parseData(responseBody)
    }

}