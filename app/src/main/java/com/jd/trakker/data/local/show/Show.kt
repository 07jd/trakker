package com.jd.trakker.data.local.show

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private const val imageEndpoint = "https://image.tmdb.org/t/p/"

@Suppress("PropertyName")
@Entity(tableName = "shows")
data class Show(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val adult: Boolean,
    val genres: List<String>,
    val name: String,
    val first_air_date: String,
    val overview: String,
    val vote_average: Double,
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
        val dateComponents = first_air_date.split("-").mapNotNull { it.toIntOrNull() }
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