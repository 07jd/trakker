package com.jd.trakker.data.local.genre

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShowGenreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(genres: List<ShowGenre>)

    @Query("SELECT * FROM showGenre WHERE id = :id")
    suspend fun getByID(id: Int): ShowGenre?

    @Query("SELECT * FROM showGenre")
    suspend fun getAllGenres(): List<ShowGenre>
}

@Dao
interface MovieGenreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(genres: List<MovieGenre>)

    @Query("SELECT * FROM movieGenre WHERE id = :id")
    suspend fun getByID(id: Int): MovieGenre?

    @Query("SELECT * FROM movieGenre")
    suspend fun getAllGenres(): List<MovieGenre>
}