package com.jd.trakker.Api.TMDB.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("movieGenre")
data class MovieGenre(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String
)

@Entity("showGenre")
data class ShowGenre(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String
)