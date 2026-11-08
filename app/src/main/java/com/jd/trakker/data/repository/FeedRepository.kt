package com.jd.trakker.data.repository

import com.jd.trakker.data.local.item.ItemType
import com.jd.trakker.data.local.feed.CachedFeed
import com.jd.trakker.data.local.feed.CacheFeedDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class FeedRepository(
    private val cachedFeedsDao: CacheFeedDao
) {
    suspend fun getCachedFeed(type: ItemType, page: Int = 1, minutes: Int = 5): List<Int>? {
        return withContext(Dispatchers.IO) {
            cachedFeedsDao.clearOldFeeds(type = type, minutes = minutes, page = page)
            cachedFeedsDao.getFeedResult(
                minutes = minutes,
                type = type,
                page = page
            )?.ids
        }
    }

    suspend fun cacheFeed(type: ItemType, page: Int, ids: List<Int>) {
        withContext(Dispatchers.IO) {
            cachedFeedsDao.insertFeedResult(
                CachedFeed(
                    type = type,
                    page = page,
                    ids = ids
                )
            )
        }
    }
}