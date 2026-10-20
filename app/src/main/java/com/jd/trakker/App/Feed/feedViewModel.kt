package com.jd.trakker.App.Feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jd.trakker.Api.igdb.client.GameFetcher
import com.jd.trakker.Api.igdb.db.GameDao
import com.jd.trakker.App.Feed.db.FeedResult
import com.jd.trakker.App.Feed.db.FeedResultDao
import com.jd.trakker.App.Feed.db.FeedType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class feedViewModel(
    private val gameDao: GameDao,
    private val gameFetcher: GameFetcher,
    private val feedResultDao: FeedResultDao
) : ViewModel() {
    private val _state = MutableStateFlow(FeedState())
    val uiState: StateFlow<FeedState> = _state.asStateFlow()


    init {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                getFeedPage(1, 5, true)
                _state.update { it.copy(loading = false) }
            } catch (e: Exception) {
                println("Init feed get fail, ${e.message}")
                val data = when (_state.value.feedType) {
                    FeedType.Games -> withContext(Dispatchers.IO) {
                        gameDao.getAllGames()
                    }.map { FeedGame(it) }

                    FeedType.Movies -> TODO()
                    FeedType.Shows -> TODO()
                    FeedType.Books -> TODO()
                }

                _state.update { it.copy(loading = false, feed = data, usingOfflineDataFeed = true) }
            }
        }
    }

    // TODO: Generalizar essa func para obter qualquer feed
    // Gets a new feed and caches it on the feedDB
    suspend fun getFeedPage(
        page: Int,
        time: Int = 5,
        resetFeed: Boolean = false,
        forceFetch: Boolean = false
    ) {
        // Try to fetch from cached feeds
        val type = _state.value.feedType
        val cachedFeed = withContext(Dispatchers.IO) {
            feedResultDao.getFeedResult(time, type, page)
        }

        // Got a Cached Feed!
        val feedData = if (cachedFeed != null && !forceFetch) {
            println("Page requested is cached!")
            withContext(Dispatchers.IO) {
                when (type) {
                    FeedType.Games -> {
                        gameDao.getGameBatchById(cachedFeed.ids).map { FeedGame(it) }
                    }

                    FeedType.Movies -> TODO()
                    FeedType.Shows -> TODO()
                    FeedType.Books -> TODO()
                }
            }
        } else {
            // TODO: Failed fetching SHOULD NOT CRASH THE APP!
            println("Fetching requested page!")
            when (type) {
                FeedType.Games -> {
                    val games = gameFetcher.getFeed(page)
                    withContext(Dispatchers.IO) {
                        games.forEach { if (!gameDao.hasCompleteData(it.id)) gameDao.insertGame(it) }

                        val ids = games.map { it.id }
                        feedResultDao.insertFeedResult(
                            FeedResult(
                                ids = ids,
                                type = type,
                                page = page
                            )
                        )
                    }

                    games.map { FeedGame(it) }
                }

                FeedType.Movies -> TODO()
                FeedType.Shows -> TODO()
                FeedType.Books -> TODO()
            }
        }

        if (resetFeed) {
            _state.update { it.copy(feed = feedData) }
        } else {
            // TODO: MELHORAR
            _state.update { it.copy(feed = _state.value.feed + feedData) }
        }
    }

    suspend fun search(query: String): List<FeedItem> {
        val data = when (_state.value.feedType) {
            FeedType.Games -> {
                gameFetcher.searchGameByName(query, 1).map {
                    FeedGame(it)
                }
            }

            FeedType.Movies -> TODO()
            FeedType.Shows -> TODO()
            FeedType.Books -> TODO()
        }

        // Cache on DB
        withContext(Dispatchers.IO) {
            data.forEach {
                when (it) {
                    is FeedGame -> if (!gameDao.hasCompleteData(it.game.id)) gameDao.insertGame(it.game)
                }
            }
        }

        return data
    }

    fun onEvent(event: FeedEvent) {
        when (event) {
            is FeedEvent.focus -> {
                viewModelScope.launch {
                    _state.update { it.copy(focused = event.item) }
                }
            }
            FeedEvent.unfocus -> {
                _state.update { it.copy(focused = null) }
            }
            is FeedEvent.changeFeedType -> {
                if (_state.value.feedType == event.type) return
                _state.update { it.copy(feedType = event.type, loading = true, pageOffset = 1) }
                viewModelScope.launch {
                    getFeedPage(1)
                }
                _state.update { it.copy(loading = false) }
            }
            FeedEvent.refresh -> {
                viewModelScope.launch {
                    _state.update { it.copy(isRefreshing = true) }
                    try {
                        if (_state.value.showingSearchResults) {
                            // Update search results
                            val newData = search(_state.value.searchTerm)
                            _state.update {
                                it.copy(
                                    searchResult = newData,
                                    usingOfflineDataSearch = false,
                                    isRefreshing = false
                                )
                            }
                        } else {
                            // Update feed
                            getFeedPage(1, 5, resetFeed = true, forceFetch = true)
                            _state.update { it.copy(isRefreshing = false, usingOfflineDataFeed = false) }
                        }
                    } catch (e: Exception) {
                        println("Exception on refresh: ${e.message}")
                        _state.update { it.copy(isRefreshing = false, refreshFailed = true) }
                    }
                }
            }
            FeedEvent.clearRefreshFail -> {
                _state.update { it.copy(refreshFailed = false) }
            }
            FeedEvent.nextPage -> {
                viewModelScope.launch {
                    _state.update { it.copy(nextPageLoading = true) }
                    try {
                        val nextPage = _state.value.pageOffset + 1
                        getFeedPage(nextPage)
                        _state.update { it.copy(pageOffset = nextPage, nextPageLoading = false) }
                    } catch (e: Exception) {
                        println("Next loading page failed! ${e.message}")
                        _state.update { it.copy(nextPageLoading = false) }
                    }
                }
            }
            is FeedEvent.search -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            loading = true,
                            searchResult = emptyList(),
                            showingSearchResults = true
                        )
                    }
                    try {
                        val data = search(event.name)
                        _state.update {
                            it.copy(
                                loading = false,
                                searchResult = data,
                                searchTerm = event.name,
                                showingSearchResults = true,
                                usingOfflineDataSearch = false
                            )
                        }
                    } catch (e: Exception) {
                        println("Search failed, ${e.message}, getting results from db")
                        val data = withContext(Dispatchers.IO) {
                            when (_state.value.feedType) {
                                FeedType.Games -> gameDao.getGamesByName(event.name).map { FeedGame(it) }
                                FeedType.Movies -> TODO()
                                FeedType.Shows -> TODO()
                                FeedType.Books -> TODO()
                            }
                        }

                        _state.update {
                            it.copy(
                                loading = false,
                                searchTerm = event.name,
                                searchResult = data,
                                showingSearchResults = true,
                                usingOfflineDataSearch = true
                            )
                        }
                    }
                }
            }
            FeedEvent.closeSearch -> {
                // Can cancel mid-fetch
                _state.update {
                    it.copy(
                        loading = false,
                        searchTerm = "",
                        searchResult = emptyList(),
                        showingSearchResults = false,
                        isRefreshing = false
                    )
                }
            }
        }
    }
}