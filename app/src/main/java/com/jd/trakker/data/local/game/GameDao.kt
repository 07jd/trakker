package com.jd.trakker.data.local.game

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(games: List<Game>)

    @Query("SELECT * FROM game WHERE id = :gameID")
    suspend fun getGameByID(gameID: Int): Game?

    @Query("SELECT * FROM game WHERE name = :gameName")
    suspend fun getGameByExactName(gameName: String): Game?

    @Query("SELECT * FROM game WHERE name LIKE :gameName")
    suspend fun getByName(gameName: String): List<Game>

    @Query("SELECT * FROM game ORDER BY rating DESC")
    suspend fun getAll(): List<Game>

    // The API query returns sorted DESC by rating so to keep consistency returns sorted
    @Query("SELECT * FROM game WHERE id IN (:ids) ORDER BY rating DESC")
    suspend fun getBatchByID(ids: List<Int>): List<Game>
}