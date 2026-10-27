package com.jd.trakker.data.repository

import com.jd.trakker.data.local.ItemType
import com.jd.trakker.data.local.feed.FeedResult
import com.jd.trakker.data.local.feed.FeedResultDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class FeedRepository(
    private val cachedFeedsDao: FeedResultDao
) {
    suspend fun getCachedFeed(type: ItemType, page: Int = 1, minutes: Int = 5): List<Int>? {
        return withContext(Dispatchers.IO) {
            cachedFeedsDao.clearOldFeeds(type, minutes, page)
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
                FeedResult(
                    type = type,
                    page = page,
                    ids = ids
                )
            )
        }
    }
}