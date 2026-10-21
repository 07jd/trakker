package com.jd.trakker.api.tmdb.db

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

    @Query("SELECT * FROM movies ORDER BY vote_average DESC")
    suspend fun getAll(): List<Movie>

    @Query("SELECT * FROM movies WHERE id IN (:ids)")
    suspend fun getBatchByID(ids: List<Int>): List<Movie>
}