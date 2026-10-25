package com.jd.trakker.data.local.tracked

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jd.trakker.ui.components.ItemType

@Dao
interface TrackedItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: TrackedItem)

    @Delete
    suspend fun remove(item: TrackedItem)

    @Update
    suspend fun update(item: TrackedItem)

    @Query("SELECT * FROM tracked")
    suspend fun getAll(): List<TrackedItem>

    @Query("SELECT * FROM tracked WHERE type = :type")
    suspend fun getAllByType(type: ItemType): List<TrackedItem>

    @Query("SELECT EXISTS(SELECT 1 FROM tracked WHERE id = :id AND type = :type)")
    suspend fun exists(id: Int, type: ItemType): Boolean
}