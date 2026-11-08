package com.jd.trakker.ui.tracked

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jd.trakker.data.local.item.GameItem
import com.jd.trakker.data.local.item.Item
import com.jd.trakker.data.local.item.MovieItem
import com.jd.trakker.data.local.item.ShowItem
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
                    favoriteItems   = favorite,
                    loading         = false
                )
            }
        }
    }

    private suspend fun getData(status: TrackedStatus): List<Item> {
        return itemRepository.getAll(itemRepository.getAllTracked(status))
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
                            favoriteItems   = favorite,
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
            }
        }
    }
}