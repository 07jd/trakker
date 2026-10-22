package com.jd.trakker.App.Feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jd.trakker.api.igdb.client.GameFetcher
import com.jd.trakker.api.igdb.db.GameDao
import com.jd.trakker.App.Feed.db.FeedResult
import com.jd.trakker.App.Feed.db.FeedResultDao
import com.jd.trakker.App.Feed.db.FeedType
import com.jd.trakker.api.ExceptionAPI
import com.jd.trakker.api.igdb.db.Game
import com.jd.trakker.api.tmdb.client.GenreFetcher
import com.jd.trakker.api.tmdb.client.MovieFetcher
import com.jd.trakker.api.tmdb.client.ShowFetcher
import com.jd.trakker.api.tmdb.db.MovieDao
import com.jd.trakker.api.tmdb.db.MovieGenreDao
import com.jd.trakker.api.tmdb.db.ShowDao
import com.jd.trakker.api.tmdb.db.ShowGenreDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class feedViewModel(
    private val gameDao: GameDao,
    private val movieDao: MovieDao,
    private val showDao: ShowDao,
    private val showGenreDao: ShowGenreDao,
    private val movieGenreDao: MovieGenreDao,
    private val feedResultDao: FeedResultDao
) : ViewModel() {
    // Online Data fetchers
    private val gameFetcher: GameFetcher by lazy { GameFetcher() }
    private val movieFetcher: MovieFetcher by lazy { MovieFetcher() }
    private val showFetcher: ShowFetcher by lazy { ShowFetcher() }
    private val genreFetcher: GenreFetcher by lazy { GenreFetcher() }


    private val _state = MutableStateFlow(FeedState())
    val uiState: StateFlow<FeedState> = _state.asStateFlow()
    val feedExpirationTimeInMinutes = 5

    // On init behavior
    init {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
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
        val cachedFeedData: List<FeedItem>? = if (cachedFeed != null && !forceFetch) {
            println("Requested Feed Page is cached")
            withContext(Dispatchers.IO) {
                when (feedType) {
                    FeedType.Games  -> gameDao.getBatchByID(cachedFeed.ids).map { FeedGame(it) }
                    FeedType.Movies -> movieDao.getBatchByID(cachedFeed.ids).map { FeedMovie(it) }
                    FeedType.Shows  -> showDao.getBatchByID(cachedFeed.ids).map { FeedShow(it) }
                }
            }
        } else { null }

        val feedData: List<FeedItem> = if (cachedFeedData == null) {
            val (ids, data) = when (feedType) {
                FeedType.Games -> {
                    val games = gameFetcher.getFeed(page)
                    withContext(Dispatchers.IO) { gameDao.insert(games) }
                    Pair(games.map { it.id }, games.map { FeedGame(it) })
                }
                FeedType.Movies -> {
                    val movies = movieFetcher.getFeed(page)
                    withContext(Dispatchers.IO) { movieDao.insert(movies) }
                    Pair(movies.map { it.id }, movies.map { FeedMovie(it) })
                }
                FeedType.Shows -> {
                    val shows = showFetcher.getFeed(page)
                    withContext(Dispatchers.IO) { showDao.insert(shows) }
                    Pair(shows.map { it.id }, shows.map { FeedShow(it) })
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
                            is FeedGame -> item.game.id
                            is FeedMovie -> item.movie.id
                            is FeedShow -> item.show.id
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
                FeedType.Games  -> gameDao.getAll().map { FeedGame(it) }
                FeedType.Movies -> movieDao.getAll().map { FeedMovie(it) }
                FeedType.Shows  -> showDao.getAll().map { FeedShow(it) }
            }
        }

        _state.update { it.copy(feed = offlineFeedData, usingOfflineDataFeed = true) }
    }

    // Search for something via a query
    // Caches results on the respective DB
    // Throws when fails to search (always searches online)
    suspend fun search(query: String): List<FeedItem> {
        // Fetch and Cache data
        val data = when (_state.value.feedType) {
            FeedType.Games  -> {
                val data = gameFetcher.searchGameByName(query, 1)
                withContext(Dispatchers.IO) { gameDao.insert(data) }
                data.map { FeedGame(it) }
            }
            FeedType.Movies -> {
                println("MOVIES: im going to fetch the data!")
                val data = movieFetcher.search(query, 1)
                println("MOVIES: data $data")
                withContext(Dispatchers.IO) { movieDao.insert(data) }
                data.map { FeedMovie(it) }
            }
            FeedType.Shows  -> {
                val data = showFetcher.search(query, 1)
                withContext(Dispatchers.IO) { showDao.insert(data) }
                data.map { FeedShow(it) }
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
                                FeedType.Games  -> gameDao.getGamesByName(event.name).map { FeedGame(it) }
                                FeedType.Movies -> movieDao.getByName(event.name).map { FeedMovie(it) }
                                FeedType.Shows  -> showDao.getByName(event.name).map { FeedShow(it) }
                            }
                        }

                        // TODO: Maybe do this be a setting, that user can set?
                        // Do not set operation failed, user is already informed visually via a
                        // banner on the top telling him that its using 'offline data'
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