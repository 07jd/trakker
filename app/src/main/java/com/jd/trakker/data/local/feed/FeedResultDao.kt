package com.jd.trakker.data.local.feed

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jd.trakker.data.local.ItemType

@Dao
interface FeedResultDao {
    @Insert
    suspend fun insertFeedResult(feed: FeedResult)

    @Query("""
        DELETE FROM feedResult 
        WHERE type = :type 
        AND timestamp < (:now-:minutes*60*1000)
        AND page = :page
    """)
    suspend fun clearOldFeeds(type: ItemType, page: Int = 1, minutes: Int = 5, now: Long = System.currentTimeMillis())

    @Query(
        """
        SELECT *
        FROM feedResult
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
    ): FeedResult?
}