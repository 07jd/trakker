package com.jd.trakker.api.igdb.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jd.trakker.api.ListConverter

@Database(
    entities = [Game::class],
    version = 1
)
@TypeConverters(ListConverter::class)
abstract class GameDatabase: RoomDatabase() {
    abstract val dao: GameDao
}