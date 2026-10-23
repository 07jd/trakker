package com.jd.trakker.data.local.game

import androidx.room.Entity
import androidx.room.PrimaryKey

private const val imageEndpoint = "https://images.igdb.com/igdb/image/upload/t_"

@Entity(tableName = "game")
data class Game(
    @PrimaryKey(autoGenerate = false)
    val id: Int,                        // ID provided by igdb
    val name: String,                   // Game name
    val rating: Double?,                // 0-100
    val storyline: String?,             // Talks about the game story
    val summary: String?,               // Talks about the game (more gameplaywise)
    val url: String?,                   // igdb game url page
    val artworks: List<String>?,        // Image IDS of the artworks
    val cover: String,                  // Image ID of the cover of the game
    val platforms: List<String>?,       // Platforms that the game can be play (X360,PS3,PC,...)
    val similarGames: List<Int>?        // List of similar games IDS
) {
    fun getCoverURL(): String {
        return "${imageEndpoint}cover_big/${cover}.jpg"
    }

    fun getBannerURL(artworkID: String): String {
        return "${imageEndpoint}1080p/${artworkID}.jpg"
    }
}