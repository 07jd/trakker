package com.jd.trakker.data.local.genre

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MovieGenre::class],
    version = 1,
    exportSchema = false
)
abstract class MovieGenreDatabase: RoomDatabase() {
    abstract val dao: MovieGenreDao
}

@Database(
    entities = [ShowGenre::class],
    version = 1,
    exportSchema = false
)
abstract class ShowGenreDatabase: RoomDatabase() {
    abstract val dao: ShowGenreDao
}