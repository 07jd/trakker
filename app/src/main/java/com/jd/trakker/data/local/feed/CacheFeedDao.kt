package com.jd.trakker.data.local.feed

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jd.trakker.data.local.ItemType

@Dao
interface CacheFeedDao {
    @Insert
    suspend fun insertFeedResult(feed: CachedFeed)

    @Query("""
        DELETE FROM cachedFeed 
        WHERE type = :type 
        AND timestamp < (:now-:minutes*60*1000)
        AND page = :page
    """)
    suspend fun clearOldFeeds(type: ItemType, page: Int = 1, minutes: Int = 5, now: Long = System.currentTimeMillis())

    @Query(
        """
        SELECT *
        FROM cachedFeed
        WHERE timestamp >= (:now-:minutes*60*1000) and type = :type and page = :page
        ORDER BY timestamp DESC
        LIMIT 1
    """
    )
    suspend fun getFeedResult(
        minutes: Int,
        type: ItemType,
        page: Int = 1,
        now: Long = System.currentTimeMillis()
    ): CachedFeed?
}