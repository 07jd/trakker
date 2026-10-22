package com.jd.trakker.data.local.game

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jd.trakker.data.local.ListConverter

@Database(
    entities = [Game::class],
    version = 1
)
@TypeConverters(ListConverter::class)
abstract class GameDatabase: RoomDatabase() {
    abstract val dao: GameDao
}