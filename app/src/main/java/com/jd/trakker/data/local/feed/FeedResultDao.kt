package com.jd.trakker.data.local.feed

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FeedResultDao {
    @Insert
    suspend fun insertFeedResult(feed: FeedResult)

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
        type: FeedType,
        page: Int = 1,
        now: Long = System.currentTimeMillis()
    ): FeedResult?
}