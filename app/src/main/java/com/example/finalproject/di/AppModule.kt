package com.example.finalproject.di

import android.content.Context
import com.example.finalproject.data.repository.GamesRepositoryImpl
import com.example.finalproject.domain.repository.GamesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideGamesRepository(repository: GamesRepositoryImpl): GamesRepository {
        return repository
    }
}
