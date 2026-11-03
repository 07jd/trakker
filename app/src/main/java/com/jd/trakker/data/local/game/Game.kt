package com.jd.trakker.data.local.game

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jd.trakker.R
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private const val imageEndpoint = "https://images.igdb.com/igdb/image/upload/t_"

@Suppress("PropertyName")
@Entity(tableName = "game")
data class Game(
    @PrimaryKey(autoGenerate = false)
    val id: Int,                        // ID provided by igdb
    val name: String,                   // Game name
    val rating: Double?,                // 0-100
    val storyline: String?,             // Talks about the game story
    val summary: String?,               // Talks about the game (more gameplaywise)
    val genres: List<String>,           // Genres
    val url: String?,                   // igdb game url page
    val artworks: List<String>?,        // Image IDS of the artworks
    val cover: String?,                 // Image ID of the cover of the game
    val platforms: List<String>?,       // Platforms that the game can be play (X360,PS3,PC,...)
    val first_release_date: Long,       // Release date (Unix timestamp)
    val similarGames: List<Int>?        // List of similar games IDS
) {
    fun getCoverURL(): String? {
        return if(cover == null) null else "${imageEndpoint}cover_big/${cover}.jpg"
    }

    fun getBannerURL(artworkID: String?): String? {
        return if(artworkID == null) null else "${imageEndpoint}1080p/${artworkID}.jpg"
    }

    fun localDateString(): String {
        return Instant.ofEpochSecond(first_release_date).atZone(ZoneOffset.UTC).toLocalDate()
                .format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                            .withLocale(Locale.getDefault())
                ).replace("/", "-")
    }

    fun platformIconRes(): List<Pair<Int?, String>> {
        if(platforms.isNullOrEmpty()) return emptyList()

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
}