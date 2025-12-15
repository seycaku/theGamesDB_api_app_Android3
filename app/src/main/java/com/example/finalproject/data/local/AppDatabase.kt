package com.example.finalproject.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(
    entities = [GameEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun gamesDao(): GamesDao
    
    companion object {
        const val DATABASE_NAME = "steam_mobile_db"
    }
}
