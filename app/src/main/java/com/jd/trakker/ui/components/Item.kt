package com.jd.trakker.ui.components

import com.jd.trakker.data.local.game.Game
import com.jd.trakker.data.local.movie.Movie
import com.jd.trakker.data.local.show.Show

sealed interface Item
data class GameItem(val game: Game)     : Item
data class MovieItem(val movie: Movie)  : Item
data class ShowItem(val show: Show)     : Item