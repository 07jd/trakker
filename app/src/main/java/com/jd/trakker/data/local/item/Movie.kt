package com.jd.trakker.data.local.item

import com.jd.trakker.data.local.metadata.Metadata
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private const val tmdbImageEndpoint = "https://image.tmdb.org/t/p/"

data class Movie(
    val id: Int,                        // ID provided by tmdb
    val url: String,                    // TMDB page of this movie
    val name: String,                   // Movie name
    val summary: String,                // Movie story summary
    val genres: List<String>,           // Genres
    val cover: String?,                 // Movie cover art
    val backdrops: List<String>,        // Movie backdrops
    val releaseDate: Long,              // Timestamp
    val rating: Double                  // 0-10
) {
    fun getCoverURL(): String? {
        return if(cover == null) null else "${tmdbImageEndpoint}w500${cover}"
    }

    fun getBannerURL(): String? {
        return if(backdrops.firstOrNull() == null) null else "${tmdbImageEndpoint}w1280${backdrops.first()}"
    }

    fun localDateString(): String {
        return Instant.ofEpochSecond(releaseDate).atZone(ZoneOffset.UTC).toLocalDate()
                .format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                            .withLocale(Locale.getDefault())
                ).replace("/", "-")
    }

    fun toMetadata(): Metadata {
        return Metadata(
            id = id,
            type = ItemType.Movie,
            name = name,
            url = url,
            summary = summary,
            genres = genres,
            cover = cover,
            backdrops = backdrops,
            releaseDate = releaseDate,
            rating = rating,
            platforms = emptyList()
        )
    }

    fun toItem(): Item {
        return MovieItem(this)
    }
}