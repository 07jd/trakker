package com.jd.trakker.data.local.tracked

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.jd.trakker.data.local.item.ItemType

@Dao
interface TrackedDao {
    @Upsert
    suspend fun upsert(data: TrackedItem)

    @Upsert
    suspend fun upsertBatch(data: List<TrackedItem>)

    @Delete
    suspend fun delete(data: TrackedItem)

    @Query("""
        SELECT *
        FROM Tracked
        WHERE id = :id AND type = :type
    """)
    suspend fun get(id: Int, type: ItemType): TrackedItem?

    @Query("""
        SELECT *
        FROM Tracked
        WHERE status != :defaultStatus
    """)
    suspend fun getAllTracked(defaultStatus: TrackedStatus = TrackedStatus.UNTRACKED): List<TrackedItem>
}