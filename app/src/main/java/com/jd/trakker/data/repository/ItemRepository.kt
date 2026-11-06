package com.jd.trakker.data.repository

import com.jd.trakker.data.local.item.Item
import com.jd.trakker.data.local.item.ItemType
import com.jd.trakker.data.local.item.getId
import com.jd.trakker.data.local.item.getType
import com.jd.trakker.data.local.item.toMetadata
import com.jd.trakker.data.local.metadata.Language
import com.jd.trakker.data.local.metadata.MetadataDao
import com.jd.trakker.data.local.metadata.toItem
import com.jd.trakker.data.local.tracked.TrackedDao
import com.jd.trakker.data.local.tracked.TrackedItem
import com.jd.trakker.data.remote.igdb.GameFetcher
import com.jd.trakker.data.remote.tmdb.MovieFetcher
import com.jd.trakker.data.remote.tmdb.ShowFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ItemRepository(
    private val metadataDao: MetadataDao,
    private val trackedDao: TrackedDao
) {
    private val gameFetcher: GameFetcher    by lazy {   GameFetcher()   }
    private val movieFetcher: MovieFetcher  by lazy {   MovieFetcher()  }
    private val showFetcher: ShowFetcher    by lazy {   ShowFetcher()   }

    // Cache metadata
    private suspend fun cacheItems(items: List<Item>) {
        withContext(Dispatchers.IO) {
            metadataDao.upsertBatch(items.map { it.toMetadata() })
        }
    }

//    suspend fun upsert(item: Item) {
//        val metadata = item.toMetadata()
//        withContext(Dispatchers.IO) {
//            metadataDao.upsert(metadata)
//        }
//    }

    @JvmName("upsertItemBatch")
    suspend fun upsertBatch(items: List<Item>) {
        val metadata = items.map { it.toMetadata() }
        withContext(Dispatchers.IO) {
            metadataDao.upsertBatch(metadata)
        }
    }

    // Search online
    suspend fun search(itemType: ItemType, query: String, page: Int = 1): List<Item> {
        val items = when(itemType) {
            ItemType.Game  -> gameFetcher.search(query, page).map { it.toItem() }
            ItemType.Movie -> movieFetcher.search(query, page).map { it.toItem() }
            ItemType.Show  -> showFetcher.search(query, page).map { it.toItem() }
        }

        cacheItems(items)
        return items
    }

    // Search on the DB
    suspend fun searchLocally(type: ItemType, query: String, language: Language = Language.EN_US): List<Item> {
        return withContext(Dispatchers.IO) {
            metadataDao.search(query, language, type).map { it.toItem() }
        }
    }

    // Get feed (online)
    suspend fun getFeed(itemType: ItemType, page: Int = 1): List<Item> {
        val items = when(itemType) {
            ItemType.Game  -> gameFetcher.getFeed(page).map { it.toItem() }
            ItemType.Movie -> movieFetcher.getFeed(page).map { it.toItem() }
            ItemType.Show  -> showFetcher.getFeed(page).map { it.toItem() }
        }

        cacheItems(items)
        return items
    }

    // Gets all items of type and id
    suspend fun getItemBatch(itemType: ItemType, ids: List<Int>): List<Item> {
        return withContext(Dispatchers.IO) {
            metadataDao.getBatch(ids, itemType).map { it.toItem() }
        }
    }

    // Get all items of a type
    suspend fun getAll(type: ItemType): List<Item> {
        return withContext(Dispatchers.IO) {
            metadataDao.getAll(type).map { it.toItem() }
        }
    }

    /*
     *  TRACKED ITEM
     */

    // Creates a default entry
    private suspend fun createTrackedDefaultValue(item: Item): TrackedItem {
        val default = TrackedItem(
            id = item.getId(),
            type = item.getType()
        )

        withContext(Dispatchers.IO) {
            trackedDao.upsert(default)
        }

        return default
    }

    // Gets all tems where status != Untracked
    suspend fun getAllTracked(): List<TrackedItem> {
        return withContext(Dispatchers.IO) {
            trackedDao.getAllTracked()
        }
    }

    // Gets TrackedItem entry, if missing creates one with default values
    suspend fun getTracked(item: Item): TrackedItem {
        return withContext(Dispatchers.IO) {
            trackedDao.get(item.getId(), item.getType()) ?: createTrackedDefaultValue(item)
        }
    }

    // Inserts/Updates batch of data
    suspend fun upsertBatch(data: List<TrackedItem>) {
        withContext(Dispatchers.IO) {
            trackedDao.upsertBatch(data)
        }
    }

    // Insert/Update data
    suspend fun upsert(data: TrackedItem) {
        withContext(Dispatchers.IO) {
            trackedDao.upsert(data)
        }
    }
}