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
import com.jd.trakker.data.local.tracked.TrackedItem
import com.jd.trakker.data.local.tracked.TrackedItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackedItemRepository(
    private val trackedItemDao: TrackedItemDao,
    private val gameDao: GameDao,
    private val movieDao: MovieDao,
    private val showDao: ShowDao,
) {
    private fun toItem(item: Game)      : Item { return GameItem(item) }
    private fun toItem(item: Movie)     : Item { return MovieItem(item) }
    private fun toItem(item: Show)      : Item { return ShowItem(item) }

    private fun getID(item: Item) : Int {
        return when(item) {
            is GameItem  -> item.game.id
            is MovieItem -> item.movie.id
            is ShowItem  -> item.show.id
        }
    }

    private fun getType(item: Item): ItemType {
        return when(item) {
            is GameItem  -> ItemType.Game
            is MovieItem -> ItemType.Movie
            is ShowItem  -> ItemType.Show
        }
    }

    suspend fun findAll(items: List<Item>): List<TrackedItem> {
        return withContext(Dispatchers.IO) {
            items.mapNotNull { trackedItemDao.find(getID(it), getType(it)) }
        }
    }

    suspend fun getAll(): List<TrackedItem> {
        val allItems =  withContext(Dispatchers.IO) {
            val list = mutableListOf<Item>()

            list.addAll(gameDao.getAll().map { toItem(it) })
            list.addAll(movieDao.getAll().map { toItem(it) })
            list.addAll(showDao.getAll().map { toItem(it) })

            list
        }

        return findAll(allItems)
    }

    suspend fun getTrackedInfo(item: Item): TrackedItem? {
        return withContext(Dispatchers.IO) {
            trackedItemDao.find(getID(item), getType(item))
        }
    }

    suspend fun createDefault(item: Item): TrackedItem {
        return withContext(Dispatchers.IO) {
            val trackedItem = TrackedItem(
                id = getID(item),
                type = getType(item),
            )

            trackedItemDao.insert(trackedItem)

            trackedItem
        }
    }

    suspend fun insertBatch(trackedItems: List<TrackedItem>) {
        withContext(Dispatchers.IO) {
            trackedItemDao.insertBatch(trackedItems)
        }
    }

    suspend fun update(trackedItem: TrackedItem) {
        withContext(Dispatchers.IO) {
            trackedItemDao.update(trackedItem)
        }
    }

//    suspend fun delete(item: Item) {
//        withContext(Dispatchers.IO) {
//            val trackedItem = trackedItemDao.find(getID(item), getType(item))
//            if(trackedItem != null) trackedItemDao.remove(trackedItem)
//        }
//    }
}