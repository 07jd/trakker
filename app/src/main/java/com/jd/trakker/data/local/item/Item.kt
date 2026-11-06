package com.jd.trakker.data.local.item

import androidx.annotation.StringRes
import com.jd.trakker.R
import com.jd.trakker.data.local.metadata.Language
import com.jd.trakker.data.local.metadata.Metadata

enum class ItemType {
    Game,
    Movie,
    Show
}

@StringRes
fun ItemType.toStringRes(): Int = when(this) {
    ItemType.Game  -> R.string.feed_type_games
    ItemType.Movie -> R.string.feed_type_movies
    ItemType.Show  -> R.string.feed_type_shows
}

sealed interface Item
data class GameItem(val game: Game)     : Item
data class MovieItem(val movie: Movie)  : Item
data class ShowItem(val show: Show)     : Item

fun Item.toMetadata(language: Language = Language.EN_US): Metadata {
    return when(this) {
        is GameItem  -> this.game.toMetadata(language)
        is MovieItem -> this.movie.toMetadata(language)
        is ShowItem  -> this.show.toMetadata(language)
    }
}

fun Item.getId(): Int {
    return when(this) {
        is GameItem -> this.game.id
        is MovieItem -> this.movie.id
        is ShowItem -> this.show.id
    }
}

fun Item.getType(): ItemType {
    return when(this) {
        is GameItem  -> ItemType.Game
        is MovieItem -> ItemType.Movie
        is ShowItem  -> ItemType.Show
    }
}