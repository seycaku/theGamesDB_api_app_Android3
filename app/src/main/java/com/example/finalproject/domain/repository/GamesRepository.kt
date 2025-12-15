package com.example.finalproject.domain.repository

import com.example.finalproject.domain.model.Game
import com.example.finalproject.domain.model.Genre
import kotlinx.coroutines.flow.Flow

interface GamesRepository {

    fun getTrendingGames(): Flow<Result<List<Game>>>

    fun getNewReleases(): Flow<Result<List<Game>>>

    fun getTopRatedGames(): Flow<Result<List<Game>>>

    suspend fun getGameDetails(id: Int): Result<Game>

    suspend fun searchGames(query: String): Result<List<Game>>

    suspend fun searchGames(
        query: String,
        genreId: Int? = null,
        ordering: String? = null
    ): Result<List<Game>>

    fun getWishlistGames(): Flow<List<Game>>

    fun getWishlistGamesSorted(sortBy: WishlistSortOption): Flow<List<Game>>

    suspend fun addToWishlist(game: Game): Result<Unit>

    suspend fun removeFromWishlist(gameId: Int): Result<Unit>

    suspend fun isInWishlist(gameId: Int): Boolean

    suspend fun getGenres(): Result<List<Genre>>

    suspend fun getGameScreenshots(gameId: Int): Result<List<String>>

    suspend fun getSimilarGames(gameId: Int): Result<List<Game>>
}

enum class WishlistSortOption {
    DATE_ADDED,
    RATING,
    NAME
}
