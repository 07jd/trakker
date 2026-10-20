package com.jd.trakker.App.Feed.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jd.trakker.Api.ListConverter

@Database(
    entities = [FeedResult::class],
    version = 1
)
@TypeConverters(ListConverter::class)
abstract class FeedResultDatabase: RoomDatabase() {
    abstract val dao: FeedResultDao
}