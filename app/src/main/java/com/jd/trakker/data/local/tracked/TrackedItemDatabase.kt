package com.jd.trakker.data.local.tracked

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jd.trakker.data.local.ListConverter

@Database(
    entities = [TrackedItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListConverter::class)
abstract class TrackedItemDatabase: RoomDatabase() {
    abstract val dao: TrackedItemDao
}