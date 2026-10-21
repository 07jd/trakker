package com.jd.trakker.Api.TMDB.DB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MovieGenre::class],
    version = 1
)
abstract class MovieGenreDatabase: RoomDatabase() {
    abstract val dao: MovieGenreDao
}

@Database(
    entities = [ShowGenre::class],
    version = 1
)
abstract class ShowGenreDatabase: RoomDatabase() {
    abstract val dao: ShowGenreDao
}