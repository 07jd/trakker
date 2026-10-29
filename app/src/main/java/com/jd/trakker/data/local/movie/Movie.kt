package com.jd.trakker.data.local.movie

import androidx.room.Entity
import androidx.room.PrimaryKey

private const val imageEndpoint = "https://image.tmdb.org/t/p/"

@Suppress("PropertyName")
@Entity("movies")
data class Movie(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val title: String,
    val adult: Boolean,
    val genres: List<String>,
    val overview: String,
    val release_date: String,
    val vote_average: Double,
    val video: Boolean,
    val backdrop_path: String?,
    val poster_path: String?,
) {
    fun getCoverURL(): String? {
        return if(poster_path == null) null else "${imageEndpoint}w500${poster_path}"
    }

    fun getBannerURL(): String? {
        return if(backdrop_path == null) null else "${imageEndpoint}w1280${backdrop_path}"
    }
}