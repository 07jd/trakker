package com.jd.trakker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jd.trakker.data.local.feed.CacheFeedDao
import com.jd.trakker.data.local.feed.CachedFeed
import com.jd.trakker.data.local.metadata.Metadata
import com.jd.trakker.data.local.metadata.MetadataDao
import com.jd.trakker.data.local.metadata.MetadataFTS
import com.jd.trakker.data.local.tracked.TrackedItem
import com.jd.trakker.data.local.tracked.TrackedDao

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        Metadata::class,
        MetadataFTS::class,
        CachedFeed::class,
        TrackedItem::class
    ]
)
@TypeConverters(ListConverter::class)
abstract class Database: RoomDatabase() {
    abstract val metadataDao    : MetadataDao
    abstract val cachedFeedDao  : CacheFeedDao
    abstract val trackedItemDao : TrackedDao
}