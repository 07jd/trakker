package com.jd.trakker.api.tmdb.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jd.trakker.api.ListConverter

@Database(
    entities = [Show::class],
    version = 1
)
@TypeConverters(ListConverter::class)
abstract class ShowDatabase: RoomDatabase() {
    abstract val dao: ShowDao
}