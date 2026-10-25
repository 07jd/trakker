package com.jd.trakker.data.local

import androidx.annotation.StringRes
import com.jd.trakker.R
import com.jd.trakker.data.local.game.Game
import com.jd.trakker.data.local.movie.Movie
import com.jd.trakker.data.local.show.Show

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