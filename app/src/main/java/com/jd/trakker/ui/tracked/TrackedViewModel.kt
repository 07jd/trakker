package com.jd.trakker.ui.tracked

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jd.trakker.data.local.item.GameItem
import com.jd.trakker.data.local.item.Item
import com.jd.trakker.data.local.item.ItemType
import com.jd.trakker.data.local.item.MovieItem
import com.jd.trakker.data.local.item.ShowItem
import com.jd.trakker.data.local.item.getType
import com.jd.trakker.data.local.tracked.TrackedStatus
import com.jd.trakker.data.repository.ItemRepository
import com.jd.trakker.ui.components.FilterDefaults
import com.jd.trakker.ui.components.FilterOrder
import com.jd.trakker.ui.components.FilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackedViewModel(
    private val itemRepository: ItemRepository
): ViewModel() {
    private val _state = MutableStateFlow(TrackedState())
    val uiState = _state.map { state ->
        val filterType  = _state.value.filterType
        val filterOrder = _state.value.filterOrder

        if(filterOrder == FilterDefaults.order && filterType == FilterDefaults.type) {
            state
        } else {
            state.copy(
                favoriteItems   = sortWithFilter(state.favoriteItems, filterOrder, filterType),
                pendingItems    = sortWithFilter(state.pendingItems, filterOrder, filterType),
                completedItems  = sortWithFilter(state.completedItems, filterOrder, filterType),
                droppedItems    = sortWithFilter(state.droppedItems, filterOrder, filterType)
            )
        }
    }

    // Get data
    init {
        viewModelScope.launch {
            // Get data and collapse empty categories
            refreshData()
            val map = _state.value.collapsedCategories.toMutableMap()
            map[TrackedStatus.PENDING.name]     = _state.value.pendingItems.isEmpty()
            map[TrackedStatus.COMPLETED.name]   = _state.value.completedItems.isEmpty()
            map[TrackedStatus.DROPPED.name]     = _state.value.droppedItems.isEmpty()
            map["favorite"]                     = _state.value.favoriteItems.isEmpty()

            calculateStats()
            _state.update {
                it.copy(loading = false, collapsedCategories = map)
            }
        }
    }

    private suspend fun getData(status: TrackedStatus): List<Item> {
        return itemRepository.getAll(itemRepository.getAllTracked(status))
    }

    // Gets all data and updates the _state
    private suspend fun refreshData() {
        val pending      = getData(TrackedStatus.PENDING)
        val completed    = getData(TrackedStatus.COMPLETED)
        val dropped      = getData(TrackedStatus.DROPPED)
        val favorite = withContext(Dispatchers.IO) {
            itemRepository.getAllFavorite().mapNotNull { itemRepository.get(it.type, it.id) }
        }

        _state.update {
            it.copy(
                pendingItems    = pending,
                completedItems  = completed,
                droppedItems    = dropped,
                favoriteItems   = favorite
            )
        }
    }

    // Calculate all the stats, updates the _state
    private suspend fun calculateStats() {
        val meanRating      = mutableMapOf<ItemType, Double>()
        val meanUserRating  = mutableMapOf<ItemType, Double>()
        val count           = mutableMapOf<ItemType, Int>()
        val genresCount     = mutableMapOf<String, Int>()
        val categoryCount   = mutableMapOf<TrackedStatus, Int>()

        val favorite = _state.value.favoriteItems
        val pending = _state.value.pendingItems
        val completed = _state.value.completedItems
        val dropped = _state.value.droppedItems

        val userRatingCount = mutableMapOf<ItemType, Int>()
        val itemRatingCount = mutableMapOf<ItemType, Int>()
        suspend fun iterate(item: Item, status: TrackedStatus? = null) {
            // Register category count
            if(status != null) categoryCount[status] = categoryCount.getOrPut(status) { 0 } + 1

            val type = item.getType()
            val (rating, genres) = when(item) {
                is GameItem  -> item.game.rating to item.game.genres
                is MovieItem -> item.movie.rating to item.movie.genres
                is ShowItem  -> item.show.rating to item.show.genres
            }
            val userRating = withContext(Dispatchers.IO) {
                itemRepository.getTracked(item).rating
            }

            // Register item type count
            count[type] = count.getOrPut(type) { 0 } + 1

            // Register ratings
            meanRating[type] = meanRating.getOrPut(type) { 0.toDouble() } + rating
            itemRatingCount[type] = itemRatingCount.getOrPut(type) { 0 } + 1

            // Register user ratings
            if(userRating != null) {
                userRatingCount[type] = userRatingCount.getOrPut(type) { 0 } + 1
                meanUserRating[type] = meanUserRating.getOrPut(type) { 0.toDouble() } + userRating
            }

            // Register genres
            genres.forEach {
                genresCount[it] = genresCount.getOrPut(it) { 0 } + 1
            }
        }

        favorite.forEach    { iterate(it) }
        pending.forEach     { iterate(it, TrackedStatus.PENDING) }
        completed.forEach   { iterate(it, TrackedStatus.COMPLETED) }
        dropped.forEach     { iterate(it, TrackedStatus.DROPPED) }

        ItemType.entries.forEach {
            meanUserRating[it]  = meanUserRating.getOrPut(it) { 0.toDouble() } / userRatingCount.getOrPut(it) { 1 }
            meanRating[it]      = meanRating.getOrPut(it) { 0.toDouble() } / itemRatingCount.getOrPut(it) { 1 }
        }

        _state.update {
            it.copy(
                stats = Stats(
                    meanRating = meanRating,
                    meanUserRating = meanUserRating,
                    count = count,
                    genresCount = genresCount,
                    categoryCount = categoryCount
                )
            )
        }
    }

    private fun sortWithFilter(
        data: List<Item>,
        order: FilterOrder,
        type: FilterType
    ): List<Item> {
        val sortedData = when(type) {
            FilterType.RELEVANCE    -> data.asReversed()
            FilterType.RATING       -> data.sortedBy {
                val rating = when(it) {
                    is GameItem  -> it.game.rating
                    is MovieItem -> it.movie.rating
                    is ShowItem  -> it.show.rating
                }

                rating
            }
            FilterType.ALPHABETICAL -> data.sortedBy {
                val name = when(it) {
                    is GameItem  -> it.game.name
                    is MovieItem -> it.movie.name
                    is ShowItem  -> it.show.name
                }

                name
            }
            FilterType.RELEASE_DATE -> data.sortedBy {
                val releaseDate = when(it) {
                    is GameItem  -> it.game.releaseDate
                    is MovieItem -> it.movie.releaseDate
                    is ShowItem  -> it.show.releaseDate
                }

                releaseDate
            }
        }

        return when(order) {
            FilterOrder.ASCENDANT  -> sortedData
            FilterOrder.DESCENDANT -> sortedData.asReversed()
        }
    }

    fun onEvent(event: TrackedEvent) {
        viewModelScope.launch {
            when(event) {
                is TrackedEvent.ChangeFilter -> _state.update { it.copy(filterType = event.type, filterOrder = event.order) }
                is TrackedEvent.ChangeCollapsedState -> {
                    val map = _state.value.collapsedCategories.toMutableMap()
                    map[event.key] = event.value
                    _state.update { it.copy(collapsedCategories = map) }
                }
                is TrackedEvent.Focus -> {
                    val trackedItem = itemRepository.getTracked(event.item)
                    _state.update { it.copy(
                        focusedItem = event.item,
                        focusedItemTracked = trackedItem
                    ) }
                }
                is TrackedEvent.Unfocus -> {
                    // Have to refresh data since it can change
                    refreshData()
                    _state.update {
                        it.copy(
                            focusedItem = null,
                            focusedItemTracked = null
                        )
                    }
                }
                is TrackedEvent.Search -> {
                    _state.update { it.copy(loading = true) }
                    val data = itemRepository.searchLocallyTrackedAndFavorite(event.query)
                    _state.update { it.copy(searchResults = data, loading = false) }
                }
                is TrackedEvent.ViewAll -> _state.update { it.copy(viewAllType = event.name) }
                is TrackedEvent.CloseViewAll -> _state.update { it.copy(viewAllType = null) }
                is TrackedEvent.CloseSearch -> _state.update { it.copy(searchResults = null) }
                is TrackedEvent.Refresh -> { refreshData(); calculateStats() }
            }
        }
    }
}