package com.jd.trakker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jd.trakker.data.local.feed.CacheFeedDao
import com.jd.trakker.data.local.feed.CachedFeed
import com.jd.trakker.data.local.game.Game
import com.jd.trakker.data.local.game.GameDao
import com.jd.trakker.data.local.movie.Movie
import com.jd.trakker.data.local.movie.MovieDao
import com.jd.trakker.data.local.show.Show
import com.jd.trakker.data.local.show.ShowDao
import com.jd.trakker.data.local.tracked.TrackedItem
import com.jd.trakker.data.local.tracked.TrackedItemDao

@Database(
    version = 1,
    exportSchema = false,
    entities = [Game::class, Movie::class, Show::class, CachedFeed::class, TrackedItem::class]
)
@TypeConverters(ListConverter::class)
abstract class Database: RoomDatabase() {
    abstract val gameDao        : GameDao
    abstract val movieDao       : MovieDao
    abstract val showDao        : ShowDao
    abstract val cachedFeedDao  : CacheFeedDao
    abstract val trackedItemDao : TrackedItemDao
}