package com.jd.trakker.data.local.game

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(games: List<Game>)

    @Query("SELECT * FROM game WHERE id = :gameID")
    fun getGameByID(gameID: Int): Flow<Game?>

    @Query("SELECT * FROM game WHERE name = :gameName")
    fun getGameByExactName(gameName: String): Flow<Game?>

    @Query("SELECT * FROM game WHERE name LIKE :gameName")
    fun getGamesByName(gameName: String): List<Game>

    @Query("SELECT * FROM game ORDER BY rating DESC")
    fun getAll(): List<Game>

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM game 
            where id = :gameID and summary IS NOT NULL
        )""")
    suspend fun hasCompleteData(gameID: Int): Boolean

    // The API query returns sorted DESC by rating so to keep consistency returns sorted
    @Query("SELECT * FROM game WHERE id IN (:ids) ORDER BY rating DESC")
    suspend fun getBatchByID(ids: List<Int>): List<Game>
}