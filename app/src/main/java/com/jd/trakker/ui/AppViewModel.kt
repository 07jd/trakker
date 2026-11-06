package com.jd.trakker.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.jd.trakker.data.local.Database
import com.jd.trakker.data.local.preferences.PreferencesManager
import com.jd.trakker.data.repository.FeedRepository
import com.jd.trakker.data.repository.ItemRepository

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
            metadataDao = database.metadataDao,
            trackedDao = database.trackedItemDao
        )
    }

    val feedRepository: FeedRepository by lazy {
        FeedRepository(database.cachedFeedDao)
    }

}