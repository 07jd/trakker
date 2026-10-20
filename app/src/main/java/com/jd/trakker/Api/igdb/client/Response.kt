package com.jd.trakker.Api.igdb.client

data class Cover(
    val id: Int,
    val image_id: String
)

data class Artwork(
    val id: Int,
    val image_id: String
)

data class Platform(
    val id: Int,
    val abbreviation: String
)

data class ShallowGameResponse(
    val id: Int,
    val cover: Cover?,
    val name: String
)

data class GameResponse(
    val id: Int,
    val artworks: List<Artwork>?,
    val cover: Cover?,
    val first_release_date: Int,
    val name: String,
    val platforms: List<Platform>?,
    val rating: Double,
    val similar_games: List<ShallowGameResponse>?,
    val storyline: String,
    val summary: String,
    val url: String
)