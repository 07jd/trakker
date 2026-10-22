package com.jd.trakker.App

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.jd.trakker.api.igdb.db.GameDatabase
import com.jd.trakker.App.Feed.db.FeedResultDatabase
import com.jd.trakker.App.settings.SettingsManager
import com.jd.trakker.api.tmdb.db.MovieDatabase
import com.jd.trakker.api.tmdb.db.MovieGenreDatabase
import com.jd.trakker.api.tmdb.db.ShowDatabase
import com.jd.trakker.api.tmdb.db.ShowGenreDatabase

class AppViewModel(
    context: Context
) : ViewModel() {
    val gamesDB: GameDatabase by lazy {
        Room.databaseBuilder<GameDatabase>(context.applicationContext, "games")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
    val moviesDB: MovieDatabase by lazy {
        Room.databaseBuilder<MovieDatabase>(context.applicationContext, "movies")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
    val showsDB: ShowDatabase by lazy {
        Room.databaseBuilder<ShowDatabase>(context.applicationContext, "shows")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
    val movieGenreDB: MovieGenreDatabase by lazy {
        Room.databaseBuilder<MovieGenreDatabase>(context.applicationContext, "movieGenre")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
    val showGenreDB: ShowGenreDatabase by lazy {
        Room.databaseBuilder<ShowGenreDatabase>(context.applicationContext, "showGenre")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
    val feedDB: FeedResultDatabase by lazy {
        Room.databaseBuilder<FeedResultDatabase>(context.applicationContext, "feedResult")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }

    val settings = SettingsManager(context)
}