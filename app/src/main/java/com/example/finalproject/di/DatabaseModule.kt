package com.example.finalproject.di

import android.content.Context
import androidx.room.Room
import com.example.finalproject.data.local.AppDatabase
import com.example.finalproject.data.local.GamesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // For development only
            .build()
    }
    

    @Provides
    fun provideGamesDao(database: AppDatabase): GamesDao {
        return database.gamesDao()
    }
}
