package com.jd.trakker.data.local.show

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShowDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(shows: List<Show>)

    @Query("SELECT * FROM shows WHERE name LIKE :name")
    suspend fun getByName(name: String): List<Show>

    @Query("SELECT * FROM shows WHERE id = :id")
    suspend fun getByID(id: Int): Show?

    @Query("SELECT * FROM shows ORDER BY vote_average DESC")
    suspend fun getAll(): List<Show>

    @Query("SELECT * FROM shows WHERE id IN (:ids)")
    suspend fun getBatchByID(ids: List<Int>): List<Show>
}