package com.jd.trakker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.jd.trakker.data.local.Item
import com.jd.trakker.data.local.ItemType
import com.jd.trakker.data.local.preferences.PreferencesManager
import com.jd.trakker.data.local.tracked.TrackedItem
import com.jd.trakker.data.repository.ItemRepository
import com.jd.trakker.data.repository.TrackedItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Json backup format
private data class Backup(
    val timestamp: Long = System.currentTimeMillis(),
    val items: List<Item>,
    val trackedItems: List<TrackedItem>
)

class SettingsViewModel(
    val itemRepository: ItemRepository,
    val trackedItemRepository: TrackedItemRepository,
    val preferences: PreferencesManager
): ViewModel() {
    private val gson by lazy { GsonBuilder().setPrettyPrinting().create() }
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

    // TODO: expose and use this to the ui
    suspend fun backup(): String = withContext(Dispatchers.IO) {
        val trackedItems = trackedItemRepository.getAll()
        val groupedItems = trackedItems.groupBy { it.type }.mapValues { (_, items) -> items.map { it.id } }

        val items = mutableListOf<Item>()
        groupedItems.forEach { (type, ids) -> itemRepository.getItemBatch(type, ids) }

        val backup = Backup(
            items = items,
            trackedItems = trackedItems
        )

        gson.toJson(backup)
    }

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
                is SettingsEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
                is SettingsEvent.Restore -> TODO()
            }
        }
    }
}