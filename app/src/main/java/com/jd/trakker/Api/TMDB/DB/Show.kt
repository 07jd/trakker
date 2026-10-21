package com.jd.trakker.Api.TMDB.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shows")
data class Show(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val adult: Boolean,
    val genre_ids: List<Int>,
    val name: String,
    val first_air_date: String,
    val overview: String,
    val vote_average: Double,
    val backdrop_path: String,
    val poster_path: String,
)