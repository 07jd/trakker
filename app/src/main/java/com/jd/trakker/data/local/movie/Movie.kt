package com.jd.trakker.data.local.movie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("movies")
data class Movie(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val title: String,
    val adult: Boolean,
    val genre_ids: List<Int>,
    val overview: String,
    val release_date: String,
    val vote_average: Double,
    val video: Boolean,
    val backdrop_path: String,
    val poster_path: String,
)