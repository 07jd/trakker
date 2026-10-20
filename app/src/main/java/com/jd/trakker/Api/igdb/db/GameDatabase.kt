package com.jd.trakker.Api.igdb.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jd.trakker.Api.ListConverter

@Database(
    entities = [Game::class],
    version = 1
)
@TypeConverters(ListConverter::class)
abstract class GameDatabase: RoomDatabase() {
    abstract val dao: GameDao
}