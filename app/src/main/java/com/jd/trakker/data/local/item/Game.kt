package com.jd.trakker.data.local.item

import com.jd.trakker.R
import com.jd.trakker.data.local.metadata.Metadata
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private const val igdbImageEndpoint = "https://images.igdb.com/igdb/image/upload/t_"

data class Game(
    val id: Int,                        // ID provided by igdb
    val url: String,                    // IGDB page of this game
    val name: String,                   // Game name
    val summary: String,                // Game story summary
    val genres: List<String>,           // Genres
    val cover: String?,                 // Game art box cover
    val backdrops: List<String>,        // Game artworks
    val platforms: List<String>,        // List of platforms where game is on
    val releaseDate: Long,              // Timestamp
    val rating: Double                  // 0-10
) {
    fun getCoverURL(): String? {
        return if(cover == null) null else "${igdbImageEndpoint}cover_big/${cover}.jpg"
    }

    fun getBannerURL(id: String): String {
        return "${igdbImageEndpoint}1080p/${id}.jpg"
    }

    fun localDateString(): String {
        return Instant.ofEpochSecond(releaseDate).atZone(ZoneOffset.UTC).toLocalDate()
                .format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                            .withLocale(Locale.getDefault())
                ).replace("/", "-")
    }

    fun platformIconRes(): List<Pair<Int?, String>> {
        if(platforms.isEmpty()) return emptyList()

        val iconRes = platforms.map {
            when(it) {
                "XBOX", "X360", "XONE", "Series X|S"             -> R.drawable.logo_xbox to it
                "PS1", "PS2", "PSP", "Vita", "PS3", "PS4", "PS5" -> R.drawable.logo_playstation to it
                "Switch 2", "Switch"                             -> R.drawable.logo_nintendo_switch to it
                "Wii"                                            -> R.drawable.logo_nintendo_wii to it
                "PSVR", "Steam VR", "Oculus VR", "Gear VR"       -> R.drawable.logo_vr to it
                "WiiU"                                           -> R.drawable.logo_nintendo_wiiu to it
                "FireTV", "Android"                              -> R.drawable.logo_android to it
                "Mobile"                                         -> R.drawable.logo_phone_fill to it
                "iOS", "Mac"                                     -> R.drawable.logo_apple to it
                "PC"                                             -> R.drawable.logo_windows to it
                "Stadia"                                         -> R.drawable.logo_stadia to it
                "Linux"                                          -> R.drawable.logo_linux to it
                else                                             -> null to it
            }
        }


        return iconRes
    }

    fun toMetadata(): Metadata {
        return Metadata(
            id = id,
            type = ItemType.Game,
            name = name,
            url = url,
            summary = summary,
            genres = genres,
            cover = cover,
            backdrops = backdrops,
            platforms = platforms,
            releaseDate = releaseDate,
            rating = rating
        )
    }

    fun toItem(): Item {
        return GameItem(this)
    }
}