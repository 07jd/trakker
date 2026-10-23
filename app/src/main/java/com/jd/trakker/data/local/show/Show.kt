package com.jd.trakker.data.local.show

import androidx.room.Entity
import androidx.room.PrimaryKey

private const val imageEndpoint = "https://image.tmdb.org/t/p/"

@Suppress("PropertyName")
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
) {
    fun getCoverURL(): String {
        return "${imageEndpoint}w500${poster_path}"
    }

    fun getBannerURL(): String {
        return "${imageEndpoint}w1280${backdrop_path}"
    }
}