package com.jd.trakker.api.igdb.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao{
    @Upsert
    fun insertGame(game: Game)

    @Query("SELECT * FROM game WHERE id = :gameID")
    fun getGameByID(gameID: Int): Flow<Game?>

    @Query("SELECT * FROM game WHERE name = :gameName")
    fun getGameByExactName(gameName: String): Flow<Game?>

    @Query("SELECT * FROM game WHERE name LIKE :gameName")
    fun getGamesByName(gameName: String): List<Game>

    @Query("SELECT * FROM game ORDER BY rating DESC")
    fun getAllGames(): List<Game>

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM game 
            where id = :gameID and summary IS NOT NULL
        )""")
    suspend fun hasCompleteData(gameID: Int): Boolean

    @Query("SELECT * FROM game WHERE id IN (:ids) ORDER BY rating DESC")
    suspend fun getGameBatchById(ids: List<Int>): List<Game>
}