package com.jd.trakker.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.jd.trakker.data.local.Database
import com.jd.trakker.data.local.preferences.PreferencesManager
import com.jd.trakker.data.repository.FeedRepository
import com.jd.trakker.data.repository.ItemRepository
import com.jd.trakker.data.repository.TrackedItemRepository

class AppViewModel(
    context: Context
) : ViewModel() {
    private val database: Database by lazy {
        Room.databaseBuilder<Database>(context.applicationContext, "data")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }

    // Public
    val preferences = PreferencesManager(context)

    val itemRepository: ItemRepository by lazy {
        ItemRepository(
            gameDao  = database.gameDao,
            movieDao = database.movieDao,
            showDao  = database.showDao,
        )
    }

    val feedRepository: FeedRepository by lazy {
        FeedRepository(database.cachedFeedDao)
    }

    val trackedItemRepository: TrackedItemRepository by lazy {
        TrackedItemRepository(
            trackedItemDao = database.trackedItemDao,
            gameDao = database.gameDao,
            movieDao = database.movieDao,
            showDao = database.showDao
        )
    }
}