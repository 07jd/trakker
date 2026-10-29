package com.jd.trakker.data.local.movie

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

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

    fun localDateString(fallback: String): String {
        val dateComponents = release_date.split("-").mapNotNull { it.toIntOrNull() }
        if(dateComponents.size != 3) return fallback

        try {
            val parsedDate = LocalDate.of(dateComponents[0],dateComponents[1],dateComponents[2])
            return parsedDate.format(
                DateTimeFormatter
                        .ofLocalizedDate(FormatStyle.MEDIUM)
                        .withLocale(Locale.getDefault())
            ).replace("/","-")
        } catch(_: Exception) { return fallback }
    }
}