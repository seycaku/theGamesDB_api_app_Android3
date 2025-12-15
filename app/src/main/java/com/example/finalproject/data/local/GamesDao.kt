package com.example.finalproject.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GamesDao {

    @Query("SELECT * FROM games ORDER BY cachedAt DESC")
    fun getAllGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGameById(id: Int): GameEntity?

    @Query("SELECT * FROM games WHERE isInWishlist = 1 ORDER BY addedToWishlistAt DESC")
    fun getWishlistGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchGames(query: String): Flow<List<GameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<GameEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    @Query("UPDATE games SET isInWishlist = :inWishlist, addedToWishlistAt = :timestamp WHERE id = :gameId")
    suspend fun updateWishlistStatus(gameId: Int, inWishlist: Boolean, timestamp: Long?)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @Query("UPDATE games SET isInWishlist = 0, addedToWishlistAt = NULL")
    suspend fun clearWishlist()

    @Query("DELETE FROM games WHERE cachedAt < :timestamp AND isInWishlist = 0")
    suspend fun deleteOldCache(timestamp: Long)
    @Query("SELECT * FROM games WHERE isInWishlist = 1 ORDER BY rating DESC")
    fun getWishlistGamesSortedByRating(): Flow<List<GameEntity>>
    
    @Query("SELECT * FROM games WHERE isInWishlist = 1 ORDER BY addedToWishlistAt DESC")
    fun getWishlistGamesSortedByDate(): Flow<List<GameEntity>>
    
    @Query("SELECT * FROM games WHERE isInWishlist = 1 ORDER BY name ASC")
    fun getWishlistGamesSortedByName(): Flow<List<GameEntity>>
}
