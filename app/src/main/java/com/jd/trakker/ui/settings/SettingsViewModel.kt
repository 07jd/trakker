package com.jd.trakker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.jd.trakker.R
import com.jd.trakker.data.local.item.GameItem
import com.jd.trakker.data.local.item.ItemType
import com.jd.trakker.data.local.item.MovieItem
import com.jd.trakker.data.local.item.ShowItem
import com.jd.trakker.data.local.backup.Backup
import com.jd.trakker.data.local.preferences.PreferencesManager
import com.jd.trakker.data.local.tracked.TrackedStatus
import com.jd.trakker.data.repository.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(
    val itemRepository: ItemRepository,
    val preferences: PreferencesManager
): ViewModel() {
    private val gson by lazy {
        GsonBuilder()
                .setPrettyPrinting()
                .create()
    }

    private val _state = MutableStateFlow(SettingsState())

    val uiState = combine(
        _state,
        // Feed settings
        combine(
            preferences.gameFeedEnabled,
            preferences.movieFeedEnabled,
            preferences.showFeedEnabled
        ) { gFeed, mFeed, sFeed -> Triple(gFeed, mFeed, sFeed) },
        // General app settings
        combine(
            preferences.useVibrations,
            preferences.theme,
            preferences.useDynamicColor
        ) { vibrate, theme, dynColor -> Triple(vibrate, theme, dynColor) },
        preferences.initialAppScreen
    ) { state, feedSettings, appSettings, initialScreen ->
        state.copy(
            useVibrations = appSettings.first,
            theme = appSettings.second,
            useDynamicColor = appSettings.third,
            gameFeedEnabled = feedSettings.first,
            movieFeedEnabled = feedSettings.second,
            showFeedEnabled = feedSettings.third,
            initialScreen = initialScreen
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        SettingsState()
    )

    fun onEvent(event: SettingsEvent) {
        viewModelScope.launch {
            when(event) {
                is SettingsEvent.ChangeDynamicColor -> preferences.setDynamicColor(event.state)
                is SettingsEvent.ChangeVibration    -> preferences.setVibrations(event.state)
                is SettingsEvent.ChangeTheme        -> preferences.setTheme(event.state)
                is SettingsEvent.ChangeFeedType -> when(event.type) {
                    ItemType.Game  -> preferences.setGameFeed(event.state)
                    ItemType.Movie -> preferences.setMovieFeed(event.state)
                    ItemType.Show  -> preferences.setShowFeed(event.state)
                }
                is SettingsEvent.ChangeInitialScreen -> preferences.setInitialAppScreen(event.state)
                is SettingsEvent.ClearFeedbackMessage -> _state.update { it.copy(feedbackMessage = null) }
                is SettingsEvent.Backup -> {
                    if(event.stream == null) {
                        _state.update { it.copy(feedbackMessage = R.string.error_file_creation) }
                        return@launch
                    }

                    val feedbackResult = withContext(Dispatchers.IO) {
                        val trackedItems = itemRepository.getAllTracked()

                        val groupedItems = trackedItems.groupBy { it.type }.mapValues { (_, items) -> items.map { it.id } }

                        val games = mutableListOf<GameItem>()
                        val movies = mutableListOf<MovieItem>()
                        val shows = mutableListOf<ShowItem>()
                        groupedItems.forEach { (type, ids) ->
                            val items = itemRepository.getItemBatch(type, ids)
                            when (type) {
                                ItemType.Game -> games.addAll(
                                    items.filterIsInstance<GameItem>()
                                )
                                ItemType.Movie -> movies.addAll(
                                    items.filterIsInstance<MovieItem>()
                                )
                                ItemType.Show -> shows.addAll(
                                    items.filterIsInstance<ShowItem>()
                                )
                            }
                        }

                        val backup = Backup(
                            games = games,
                            movies = movies,
                            shows = shows,
                            trackedItems = trackedItems
                        )

                        val data = gson.toJson(backup)
                        try {
                            event.stream.write(data.toByteArray())
                            R.string.backup_creation_success
                        } catch(e: Exception) {
                            e.printStackTrace()
                            R.string.error_backup_creation
                        } finally {
                            event.stream.close()
                        }
                    }

                    _state.update { it.copy(feedbackMessage = feedbackResult) }
                }
                is SettingsEvent.Restore -> {
                    try {
                        val parsed = gson.fromJson(event.data, Backup::class.java)
                        withContext(Dispatchers.IO) {
                            val items = buildList {
                                addAll(parsed.games)
                                addAll(parsed.movies)
                                addAll(parsed.shows)
                            }

                            itemRepository.upsertBatch(items)
                            itemRepository.upsertBatch(parsed.trackedItems)
                        }
                        _state.update { it.copy(feedbackMessage = R.string.backup_restore_success) }
                    } catch(e: Exception) {
                        e.printStackTrace()
                        _state.update { it.copy(feedbackMessage = R.string.error_data_parsing) }
                    }
                }
            }
        }
    }
}