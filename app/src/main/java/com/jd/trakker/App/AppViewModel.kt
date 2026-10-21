package com.jd.trakker.App

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.jd.trakker.api.igdb.db.GameDao
import com.jd.trakker.api.igdb.db.GameDatabase
import com.jd.trakker.App.Feed.db.FeedResultDao
import com.jd.trakker.App.Feed.db.FeedResultDatabase
import com.jd.trakker.api.tmdb.db.MovieDatabase
import com.jd.trakker.api.tmdb.db.MovieGenreDatabase
import com.jd.trakker.api.tmdb.db.ShowDatabase
import com.jd.trakker.api.tmdb.db.ShowGenreDatabase
import kotlinx.coroutines.flow.map

class AppViewModel(
    dataStore: DataStore<Preferences>,
    application: Application
) : AndroidViewModel(application) {
    val gamesDB: GameDatabase by lazy {
        Room.databaseBuilder<GameDatabase>(application.applicationContext, "games")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
    val moviesDB: MovieDatabase by lazy {
        Room.databaseBuilder<MovieDatabase>(application.applicationContext, "movies")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
    val showsDB: ShowDatabase by lazy {
        Room.databaseBuilder<ShowDatabase>(application.applicationContext, "shows")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
    val movieGenreDB: MovieGenreDatabase by lazy {
        Room.databaseBuilder<MovieGenreDatabase>(application.applicationContext, "movieGenre")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
    val showGenreDB: ShowGenreDatabase by lazy {
        Room.databaseBuilder<ShowGenreDatabase>(application.applicationContext, "showGenre")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
    val feedDB: FeedResultDatabase by lazy {
        Room.databaseBuilder<FeedResultDatabase>(application.applicationContext, "feedResult")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }

    val setupDone = dataStore.data.map { config ->
        config[AppConfig.setupDone] ?: false
    }
}