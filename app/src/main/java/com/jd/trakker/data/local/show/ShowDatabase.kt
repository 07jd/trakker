package com.jd.trakker.data.local.show

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jd.trakker.data.local.ListConverter

@Database(
    entities = [Show::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListConverter::class)
abstract class ShowDatabase: RoomDatabase() {
    abstract val dao: ShowDao
}