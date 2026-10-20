package com.jd.trakker.Api.igdb.db

import androidx.room.Entity
import androidx.room.PrimaryKey

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
)