package com.jd.trakker.data.repository

import com.jd.trakker.data.local.GameItem
import com.jd.trakker.data.local.Item
import com.jd.trakker.data.local.ItemType
import com.jd.trakker.data.local.MovieItem
import com.jd.trakker.data.local.ShowItem
import com.jd.trakker.data.local.game.Game
import com.jd.trakker.data.local.game.GameDao
import com.jd.trakker.data.local.movie.Movie
import com.jd.trakker.data.local.movie.MovieDao
import com.jd.trakker.data.local.show.Show
import com.jd.trakker.data.local.show.ShowDao
import com.jd.trakker.data.remote.igdb.GameFetcher
import com.jd.trakker.data.remote.tmdb.MovieFetcher
import com.jd.trakker.data.remote.tmdb.ShowFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ItemRepository(
    private val gameDao: GameDao,
    private val movieDao: MovieDao,
    private val showDao: ShowDao
) {
    private val gameFetcher: GameFetcher by lazy { GameFetcher() }
    private val movieFetcher: MovieFetcher by lazy { MovieFetcher() }
    private val showFetcher: ShowFetcher by lazy { ShowFetcher() }

    private fun toItem(item: Game)  : Item { return GameItem(item)
    }
    private fun toItem(item: Movie) : Item { return MovieItem(item)
    }
    private fun toItem(item: Show)  : Item { return ShowItem(item)
    }

    @JvmName("cacheGameItems")
    private suspend fun cacheItems(items: List<Game>)   { withContext(Dispatchers.IO) { gameDao.insert(items)} }
    @JvmName("cacheMovieItems")
    private suspend fun cacheItems(items: List<Movie>)  { withContext(Dispatchers.IO) { movieDao.insert(items)} }
    @JvmName("cacheShowItems")
    private suspend fun cacheItems(items: List<Show>)   { withContext(Dispatchers.IO) { showDao.insert(items)} }

    // Search online
    suspend fun search(itemType: ItemType, query: String, page: Int = 1): List<Item> {
        return when(itemType) {
            ItemType.Game  -> {
                val items = gameFetcher.search(query, page)
                cacheItems(items)
                items.map { toItem(it) }
            }
            ItemType.Movie -> {
                val items = movieFetcher.search(query, page)
                cacheItems(items)
                items.map { toItem(it) }
            }
            ItemType.Show  -> {
                val items = showFetcher.search(query, page)
                cacheItems(items)
                items.map { toItem(it) }
            }
        }
    }

    // Search on the DB
    suspend fun searchLocally(itemType: ItemType, query: String): List<Item> {
        return withContext(Dispatchers.IO) {
            when(itemType) {
                ItemType.Game  -> gameDao.getByName(query).map { toItem(it) }
                ItemType.Movie -> movieDao.getByName(query).map { toItem(it) }
                ItemType.Show  -> showDao.getByName(query).map { toItem(it) }
            }
        }
    }

    // Get feed (online)
    suspend fun getFeed(itemType: ItemType, page: Int = 1): List<Item> {
        return when(itemType) {
            ItemType.Game  -> {
                val items = gameFetcher.getFeed(page)
                cacheItems(items)
                items.map { toItem(it) }
            }
            ItemType.Movie -> {
                val items = movieFetcher.getFeed(page)
                cacheItems(items)
                items.map { toItem(it) }
            }
            ItemType.Show  -> {
                val items = showFetcher.getFeed(page)
                cacheItems(items)
                items.map { toItem(it) }
            }
        }
    }

    // Gets all items of type and id
    suspend fun getItemBatch(itemType: ItemType, ids: List<Int>): List<Item> {
        return withContext(Dispatchers.IO) {
            when(itemType) {
                ItemType.Game  -> gameDao.getBatchByID(ids).map { toItem(it) }
                ItemType.Movie -> movieDao.getBatchByID(ids).map { toItem(it) }
                ItemType.Show  -> showDao.getBatchByID(ids).map { toItem(it) }
            }
        }
    }

    suspend fun getAll(itemType: ItemType): List<Item> {
        return withContext(Dispatchers.IO) {
            when(itemType) {
                ItemType.Game  -> gameDao.getAll().map { toItem(it) }
                ItemType.Movie -> movieDao.getAll().map { toItem(it) }
                ItemType.Show  -> showDao.getAll().map { toItem(it) }
            }
        }
    }
}