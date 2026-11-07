package com.jd.trakker.data.local.item

import com.jd.trakker.data.local.metadata.Metadata
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private const val tmdbImageEndpoint = "https://image.tmdb.org/t/p/"

data class Show(
    val id: Int,                        // ID provided by tmdb
    val url: String,                    // TMDB page of this show
    val name: String,                   // Show name
    val summary: String,                // Show story summary
    val genres: List<String>,           // Genres
    val cover: String?,                 // Show cover art
    val backdrops: List<String>,        // Show backdrops
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
        return Instant.ofEpochMilli(releaseDate).atZone(ZoneOffset.UTC).toLocalDate()
                .format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                            .withLocale(Locale.getDefault())
                ).replace("/", "-")
    }

    fun toMetadata(): Metadata {
        return Metadata(
            id = id,
            type = ItemType.Show,
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
        return ShowItem(this)
    }
}