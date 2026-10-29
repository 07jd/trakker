package com.jd.trakker.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.jd.trakker.data.local.game.GameDatabase
import com.jd.trakker.data.local.feed.FeedResultDatabase
import com.jd.trakker.data.local.preferences.PreferencesManager
import com.jd.trakker.data.local.movie.MovieDatabase
import com.jd.trakker.data.local.show.ShowDatabase
import com.jd.trakker.data.local.tracked.TrackedItemDatabase
import com.jd.trakker.data.repository.FeedRepository
import com.jd.trakker.data.repository.ItemRepository
import com.jd.trakker.data.repository.TrackedItemRepository

class AppViewModel(
    context: Context
) : ViewModel() {
    private val gamesDB: GameDatabase by lazy {
        Room.databaseBuilder<GameDatabase>(context.applicationContext, "games")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
    private val moviesDB: MovieDatabase by lazy {
        Room.databaseBuilder<MovieDatabase>(context.applicationContext, "movies")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
    private val showsDB: ShowDatabase by lazy {
        Room.databaseBuilder<ShowDatabase>(context.applicationContext, "shows")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
    private val trackedItemDB: TrackedItemDatabase by lazy {
        Room.databaseBuilder<TrackedItemDatabase>(context.applicationContext, "tracked")
                .setDriver(AndroidSQLiteDriver())
                .build()
    }
    val feedDB: FeedResultDatabase by lazy {
        Room.databaseBuilder<FeedResultDatabase>(context.applicationContext, "feedResult")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }

    // Public
    val preferences = PreferencesManager(context)

    val itemRepository: ItemRepository by lazy {
        ItemRepository(
            gameDao  = gamesDB.dao,
            movieDao = moviesDB.dao,
            showDao  = showsDB.dao,
        )
    }

    val feedRepository: FeedRepository by lazy {
        FeedRepository(feedDB.dao)
    }

    val trackedItemRepository: TrackedItemRepository by lazy {
        TrackedItemRepository(
            trackedItemDao = trackedItemDB.dao,
            gameDao = gamesDB.dao,
            movieDao = moviesDB.dao,
            showDao = showsDB.dao
        )
    }
}