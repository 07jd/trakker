package com.jd.trakker.Api.TMDB.DB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movies: List<Movie>)

    @Query("SELECT * FROM movies WHERE title LIKE :name")
    suspend fun getByName(name: String): List<Movie>

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getByID(id: Int): Movie?

    @Query("SELECT * FROM movies")
    suspend fun getAll(): List<Movie>
}