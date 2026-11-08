package com.jd.trakker.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.jd.trakker.data.local.Database
import com.jd.trakker.data.local.preferences.PreferencesManager
import com.jd.trakker.data.local.preferences.PreferencesState
import com.jd.trakker.data.repository.FeedRepository
import com.jd.trakker.data.repository.ItemRepository
import kotlinx.coroutines.flow.combine

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
    val preferencesFlow = combine(
        combine(
            preferences.gameFeedEnabled,
            preferences.movieFeedEnabled,
            preferences.showFeedEnabled
        ) { game, movie, show -> Triple(game, movie, show) },
        combine(
            preferences.theme,
            preferences.useDynamicColor,
            preferences.useVibrations
        ) { theme, dynColor, vib -> Triple(theme, dynColor, vib) },
        preferences.initialAppScreen,
        preferences.initialSetupDone
    ) { feeds, themeOptions, initialScreen, setupDone ->
        PreferencesState(
            isInitialSetupDone = setupDone,
            initialAppScreen = initialScreen,
            themeType = themeOptions.first,
            useDynamicColor = themeOptions.second,
            useVibrations = themeOptions.third,
            gameFeedEnabled = feeds.first,
            movieFeedEnabled = feeds.second,
            showFeedEnabled = feeds.third
        )
    }

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