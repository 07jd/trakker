package com.jd.trakker.Api.TMDB.DB

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jd.trakker.Api.ListConverter

@Database(
    entities = [Movie::class],
    version = 1
)
@TypeConverters(ListConverter::class)
abstract class MovieDatabase: RoomDatabase() {
    abstract val dao: MovieDao
}