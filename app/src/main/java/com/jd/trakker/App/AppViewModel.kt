package com.jd.trakker.App

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.jd.trakker.Api.igdb.db.GameDao
import com.jd.trakker.Api.igdb.db.GameDatabase
import com.jd.trakker.App.Feed.db.FeedResultDao
import com.jd.trakker.App.Feed.db.FeedResultDatabase
import kotlinx.coroutines.flow.map

class AppViewModel(
    dataStore: DataStore<Preferences>,
    application: Application
) : AndroidViewModel(application) {
    private val gamesDB: GameDatabase by lazy {
        Room.databaseBuilder<GameDatabase>(application.applicationContext, "games")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }

    private val feedDB: FeedResultDatabase by lazy {
        Room.databaseBuilder<FeedResultDatabase>(application.applicationContext, "feedResult")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }

    val setupDone = dataStore.data.map { config ->
        config[AppConfig.setupDone] ?: false
    }

    fun getGamesDao(): GameDao {
        return gamesDB.dao
    }

    fun getFeedResultDao(): FeedResultDao {
        return feedDB.dao
    }
}