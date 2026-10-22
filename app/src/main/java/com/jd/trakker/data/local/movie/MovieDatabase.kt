package com.jd.trakker.data.local.movie

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jd.trakker.data.local.ListConverter

@Database(
    entities = [Movie::class],
    version = 1
)
@TypeConverters(ListConverter::class)
abstract class MovieDatabase: RoomDatabase() {
    abstract val dao: MovieDao
}