package com.jd.trakker.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jd.trakker.data.remote.igdb.GameFetcher
import com.jd.trakker.data.local.game.GameDao
import com.jd.trakker.data.local.feed.FeedResult
import com.jd.trakker.data.local.feed.FeedResultDao
import com.jd.trakker.data.remote.ExceptionAPI
import com.jd.trakker.data.remote.tmdb.GenreFetcher
import com.jd.trakker.data.remote.tmdb.MovieFetcher
import com.jd.trakker.data.remote.tmdb.ShowFetcher
import com.jd.trakker.data.local.movie.MovieDao
import com.jd.trakker.data.local.genre.MovieGenreDao
import com.jd.trakker.data.local.show.ShowDao
import com.jd.trakker.data.local.genre.ShowGenreDao
import com.jd.trakker.data.local.preferences.PreferencesManager
import com.jd.trakker.ui.components.GameItem
import com.jd.trakker.ui.components.Item
import com.jd.trakker.ui.components.ItemType
import com.jd.trakker.ui.components.MovieItem
import com.jd.trakker.ui.components.ShowItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedViewModel(
    private val preferences: PreferencesManager,
    private val gameDao: GameDao,
    private val movieDao: MovieDao,
    private val showDao: ShowDao,
    val showGenreDao: ShowGenreDao,
    val movieGenreDao: MovieGenreDao,
    private val feedResultDao: FeedResultDao
) : ViewModel() {
    // Online Data fetchers
    private val gameFetcher: GameFetcher by lazy { GameFetcher() }
    private val movieFetcher: MovieFetcher by lazy { MovieFetcher() }
    private val showFetcher: ShowFetcher by lazy { ShowFetcher() }

    val feedExpirationTimeInMinutes = 5
    private val _state = MutableStateFlow(FeedState())
    val uiState: StateFlow<FeedState> = combine(
        _state,
        preferences.useVibrations,
        preferences.gameFeedEnabled,
        preferences.movieFeedEnabled,
        preferences.showFeedEnabled
    ){ state, useVibrations, gameFeedEnabled, movieFeedEnabled, showFeedEnabled ->
        state.copy(
            useVibrations = useVibrations,
            enabledFeeds = buildList {
                if (gameFeedEnabled)  add(ItemType.Game)
                if (movieFeedEnabled) add(ItemType.Movie)
                if (showFeedEnabled)  add(ItemType.Show)
            }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        FeedState()
    )


    // On init behavior
    init {
        viewModelScope.launch {
            // Get enabled feeds
            val gameFeedEnabled = preferences.gameFeedEnabled.first()
            val movieFeedEnabled = preferences.movieFeedEnabled.first()
            val showFeedEnabled = preferences.showFeedEnabled.first()
            val enabledFeeds = buildList {
                if (gameFeedEnabled) add(ItemType.Game)
                if (movieFeedEnabled) add(ItemType.Movie)
                if (showFeedEnabled) add(ItemType.Show)
            }

            _state.update { it.copy(enabledFeeds = enabledFeeds, feedType = enabledFeeds.first()) }
            try {
                getFeedPage(1, feedExpirationTimeInMinutes, true)
            } catch (e: ExceptionAPI) {
                // Feed failed to load, so no valid cached feed so failed to fetch from internet
                println("Failed to get initial feed: \"${e.message}\"")
                println("Getting entries from DB")
                getOfflineFeed()
            } finally { _state.update { it.copy(loading = false) }}
        }
    }

    // Gets a new feed and caches it on the feedDB
    // Throws when fails to fetch from online sources and can't
    // This function updates the feed content of the ui state by itself, no need to update it
    suspend fun getFeedPage(
        page: Int,
        time: Int = feedExpirationTimeInMinutes,
        resetFeed: Boolean = false,
        forceFetch: Boolean = false
    ) {
        // Try to fetch from cached feeds
        val feedType = _state.value.feedType
        println("Getting a feed page of type: ${feedType.name}")
        val cachedFeed = withContext(Dispatchers.IO) { feedResultDao.getFeedResult(time, feedType, page) }

        // Retrive Cached Feed data
        val cachedFeedData: List<Item>? = if (cachedFeed != null && !forceFetch) {
            println("Requested Feed Page is cached")
            withContext(Dispatchers.IO) {
                when (feedType) {
                    ItemType.Game  -> gameDao.getBatchByID(cachedFeed.ids).map { GameItem(it) }
                    ItemType.Movie -> movieDao.getBatchByID(cachedFeed.ids).map { MovieItem(it) }
                    ItemType.Show  -> showDao.getBatchByID(cachedFeed.ids).map { ShowItem(it) }
                }
            }
        } else { null }

        val feedData: List<Item> = if (cachedFeedData == null) {
            val (ids, data) = when (feedType) {
                ItemType.Game -> {
                    val games = gameFetcher.getFeed(page)
                    withContext(Dispatchers.IO) { gameDao.insert(games) }
                    Pair(games.map { it.id }, games.map { GameItem(it) })
                }
                ItemType.Movie -> {
                    val movies = movieFetcher.getFeed(page)
                    withContext(Dispatchers.IO) { movieDao.insert(movies) }
                    Pair(movies.map { it.id }, movies.map { MovieItem(it) })
                }
                ItemType.Show -> {
                    val shows = showFetcher.getFeed(page)
                    withContext(Dispatchers.IO) { showDao.insert(shows) }
                    Pair(shows.map { it.id }, shows.map { ShowItem(it) })
                }
            }

            // Cache new feed
            withContext(Dispatchers.IO) {
                feedResultDao.insertFeedResult(FeedResult(
                    type = feedType,
                    page = page,
                    ids = ids.distinct()
                ))
            }

            data
        } else { cachedFeedData }


        if (resetFeed) {
            _state.update { it.copy(feed = feedData, pageOffset = page) }
        } else {
            _state.update {
                it.copy(
                    feed = (_state.value.feed + feedData)
                    .distinctBy { item ->
                        when (item) {
                            is GameItem -> item.game.id
                            is MovieItem -> item.movie.id
                            is ShowItem -> item.show.id
                        }
                    }, pageOffset = page
                )
            }
        }
    }

    suspend fun getOfflineFeed() {
        val feedType = _state.value.feedType
        val offlineFeedData = withContext(Dispatchers.IO) {
            when (feedType) {
                ItemType.Game  -> gameDao.getAll().map { GameItem(it) }
                ItemType.Movie -> movieDao.getAll().map { MovieItem(it) }
                ItemType.Show  -> showDao.getAll().map { ShowItem(it) }
            }
        }

        _state.update { it.copy(feed = offlineFeedData, usingOfflineDataFeed = true) }
    }

    // Search for something via a query
    // Caches results on the respective DB
    // Throws when fails to search (always searches online)
    suspend fun search(query: String): List<Item> {
        // Fetch and Cache data
        val data = when (_state.value.feedType) {
            ItemType.Game  -> {
                val data = gameFetcher.searchGameByName(query, 1)
                withContext(Dispatchers.IO) { gameDao.insert(data) }
                data.map { GameItem(it) }
            }
            ItemType.Movie -> {
                val data = movieFetcher.search(query, 1)
                withContext(Dispatchers.IO) { movieDao.insert(data) }
                data.map { MovieItem(it) }
            }
            ItemType.Show  -> {
                val data = showFetcher.search(query, 1)
                withContext(Dispatchers.IO) { showDao.insert(data) }
                data.map { ShowItem(it) }
            }
        }

        return data
    }

    fun onEvent(event: FeedEvent) {
        viewModelScope.launch {
            when (event) {
                // Switches screen from feed to item detailed screen
                // Reset any previous errors and its messages
                is FeedEvent.focus -> {
                    _state.update { it.copy(focused = event.item, operationFailed = false, failureMessage = "") }
                }

                // Remove focused item
                FeedEvent.unfocus -> {
                    _state.update { it.copy(focused = null, operationFailed = false, failureMessage = "") }
                }

                // Change feed type
                is FeedEvent.changeFeedType -> {
                    // Already on that feed type
                    if (_state.value.feedType == event.type) return@launch

                    // New feed type
                    _state.update { it.copy(feedType = event.type, loading = true, pageOffset = 1, operationFailed = false, failureMessage = "") }
                    try {
                        getFeedPage(1, feedExpirationTimeInMinutes, true)
                    } catch (e: ExceptionAPI) {
                        when(e) {
                            is ExceptionAPI.NoInternet -> {
                                getOfflineFeed()
                                _state.update { it.copy(operationFailed = true, failureMessage = e.message.toString()) }
                            }
                            else -> _state.update { it.copy(feed = emptyList(), operationFailed = true, failureMessage = e.message.toString()) }
                        }
                    } finally {
                        _state.update { it.copy(loading = false) }
                    }
                }

                // Refresh current data
                FeedEvent.refresh -> {
                        _state.update { it.copy(isRefreshing = true) }
                        try {
                            // On search screen
                            if (_state.value.showingSearchResults) {
                                val newSearchData = search(_state.value.searchTerm)
                                _state.update {
                                    it.copy(
                                        searchResult = newSearchData,
                                        usingOfflineDataSearch = false,
                                        isRefreshing = false
                                    )
                                }
                            } else {
                                // On feed screen
                                getFeedPage(1, 5, resetFeed = true, forceFetch = true)
                                _state.update {
                                    it.copy(
                                        isRefreshing = false,
                                        usingOfflineDataFeed = false,
                                        pageOffset = 1
                                    )
                                }
                            }
                        } catch (e: ExceptionAPI) {
                            println("Refresh failed due to: \"${e.message}\"")
                            when(e) {
                                // Grab offline data
                                is ExceptionAPI.NoInternet -> getOfflineFeed()
                                else -> {}
                            }

                            _state.update { it.copy(isRefreshing = false, operationFailed = true, failureMessage = e.message.toString()) }
                        }
                }

                // Clear errors
                FeedEvent.clearFailure -> {
                    _state.update { it.copy(operationFailed = false, failureMessage = "") }
                }

                // On scroll get next page of the content
                FeedEvent.nextPage -> {
                    // Wait for it to load
                    if(_state.value.nextPageLoading || _state.value.loading) return@launch

                    _state.update { it.copy(nextPageLoading = true) }
                    try {
                        val nextPage = _state.value.pageOffset + 1
                        getFeedPage(nextPage)
                        _state.update {
                            it.copy(
                                pageOffset = nextPage,
                                nextPageLoading = false
                            )
                        }
                    } catch (e: Exception) {
                        println("Failed to load next page, reason: \"${e.message}\"")
                        _state.update { it.copy(nextPageLoading = false, operationFailed = true, failureMessage = e.message.toString()) }
                    }
                }

                // Search something
                is FeedEvent.search -> {
                    _state.update {
                        it.copy(
                            loading = true,
                            showingSearchResults = true
                        )
                    }

                    // Do the search
                    try {
                        val searchResultData = search(event.name)
                        _state.update {
                            it.copy(
                                loading = false,
                                searchResult = searchResultData,
                                searchTerm = event.name,
                                showingSearchResults = true,
                                usingOfflineDataSearch = false
                            )
                        }
                    } catch (e: Exception) {
                        println("Failed fetch search results, using local results from db due to: \"${e.message}\"")
                        val localSearchData = withContext(Dispatchers.IO) {
                            when (_state.value.feedType) {
                                ItemType.Game  -> gameDao.getGamesByName(event.name).map { GameItem(it) }
                                ItemType.Movie -> movieDao.getByName(event.name).map { MovieItem(it) }
                                ItemType.Show  -> showDao.getByName(event.name).map { ShowItem(it) }
                            }
                        }

                        // Inform the user the reason of using offline data
                        _state.update {
                            it.copy(
                                loading = false,
                                searchTerm = event.name,
                                searchResult = localSearchData,
                                showingSearchResults = true,
                                usingOfflineDataSearch = true,
                                failureMessage = e.message.toString(),
                                operationFailed = true
                            )
                        }
                    }
                }

                // Return to feed 'screen' from search 'screen'
                FeedEvent.closeSearch -> {
                    // Can cancel mid-fetch
                    // Reset any errors and its messages since its switches 'screens'
                    _state.update {
                        it.copy(
                            loading = false,
                            searchTerm = "",
                            searchResult = emptyList(),
                            showingSearchResults = false,
                            isRefreshing = false,
                            operationFailed = false,
                            failureMessage = ""
                        )
                    }
                }
            }
        }
    }
}