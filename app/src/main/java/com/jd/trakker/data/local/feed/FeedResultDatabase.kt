package com.jd.trakker.data.local.feed

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jd.trakker.data.local.ListConverter

@Database(
    entities = [FeedResult::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListConverter::class)
abstract class FeedResultDatabase: RoomDatabase() {
    abstract val dao: FeedResultDao
}