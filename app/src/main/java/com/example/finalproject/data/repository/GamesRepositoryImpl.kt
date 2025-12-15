package com.example.finalproject.data.repository

import com.example.finalproject.data.local.GamesDao
import com.example.finalproject.data.mapper.GameMapper.toDomain
import com.example.finalproject.data.mapper.GameMapper.toDomainFromEntity
import com.example.finalproject.data.mapper.GameMapper.toDomainList
import com.example.finalproject.data.mapper.GameMapper.toEntity
import com.example.finalproject.data.mapper.GameMapper.toGenresMap
import com.example.finalproject.data.remote.GamesDbApi
import com.example.finalproject.domain.model.Game
import com.example.finalproject.domain.model.Genre
import com.example.finalproject.domain.repository.GamesRepository
import com.example.finalproject.domain.repository.WishlistSortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamesRepositoryImpl @Inject constructor(
    private val gamesDbApi: GamesDbApi,
    private val gamesDao: GamesDao
) : GamesRepository {

    private var genresCache: Map<Int, String>? = null

    private suspend fun getGenresMap(): Map<Int, String> {
        if (genresCache != null) return genresCache!!
        
        return try {
            val response = gamesDbApi.getGenres()
            val map = response.data?.toGenresMap() ?: emptyMap()
            genresCache = map
            map
        } catch (e: Exception) {
            emptyMap()
        }
    }

    override fun getTrendingGames(): Flow<Result<List<Game>>> = flow {
        try {
            val genresMap = getGenresMap()
            val response = gamesDbApi.getGamesByName(
                name = "mario"
            )

            val games = response.toDomainList(genresMap)

            games.forEach { game ->
                val existingEntity = gamesDao.getGameById(game.id)
                val isInWishlist = existingEntity?.isInWishlist ?: false
                val addedToWishlistAt = existingEntity?.addedToWishlistAt

                val gameWithWishlist = game.copy(
                    isInWishlist = isInWishlist,
                    addedToWishlistAt = addedToWishlistAt
                )
                gamesDao.insertGame(gameWithWishlist.toEntity())
            }

            emit(Result.success(games))

        } catch (e: IOException) {
            val cached = gamesDao.getAllGames().firstOrNull()
            if (cached != null && cached.isNotEmpty()) {
                emit(Result.success(cached.toDomainFromEntity()))
            } else {
                emit(Result.failure(e))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.onStart {
        val cached = gamesDao.getAllGames().firstOrNull()
        if (cached != null && cached.isNotEmpty()) {
            emit(Result.success(cached.toDomainFromEntity()))
        }
    }.catch { e ->
        emit(Result.failure(e))
    }

    override fun getNewReleases(): Flow<Result<List<Game>>> = flow {
        try {
            val genresMap = getGenresMap()
            val response = gamesDbApi.getGamesByName(
                name = "2024"
            )

            val games = response.toDomainList(genresMap)
                .sortedByDescending { it.released }

            games.forEach { game ->
                val existingEntity = gamesDao.getGameById(game.id)
                val isInWishlist = existingEntity?.isInWishlist ?: false
                val addedToWishlistAt = existingEntity?.addedToWishlistAt

                val gameWithWishlist = game.copy(
                    isInWishlist = isInWishlist,
                    addedToWishlistAt = addedToWishlistAt
                )
                gamesDao.insertGame(gameWithWishlist.toEntity())
            }

            emit(Result.success(games))

        } catch (e: IOException) {
            val cached = gamesDao.getAllGames().firstOrNull()
            if (cached != null && cached.isNotEmpty()) {
                emit(Result.success(cached.toDomainFromEntity()))
            } else {
                emit(Result.failure(e))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.onStart {
        val cached = gamesDao.getAllGames().firstOrNull()
        if (cached != null && cached.isNotEmpty()) {
            emit(Result.success(cached.toDomainFromEntity()))
        }
    }.catch { e ->
        emit(Result.failure(e))
    }

    override fun getTopRatedGames(): Flow<Result<List<Game>>> = flow {
        try {
            val genresMap = getGenresMap()
            val response = gamesDbApi.getGamesByName(
                name = "zelda"
            )

            val games = response.toDomainList(genresMap)
                .sortedByDescending { it.rating }

            games.forEach { game ->
                val existingEntity = gamesDao.getGameById(game.id)
                val isInWishlist = existingEntity?.isInWishlist ?: false
                val addedToWishlistAt = existingEntity?.addedToWishlistAt

                val gameWithWishlist = game.copy(
                    isInWishlist = isInWishlist,
                    addedToWishlistAt = addedToWishlistAt
                )
                gamesDao.insertGame(gameWithWishlist.toEntity())
            }

            emit(Result.success(games))

        } catch (e: IOException) {
            val cached = gamesDao.getAllGames().firstOrNull()
            if (cached != null && cached.isNotEmpty()) {
                emit(Result.success(cached.toDomainFromEntity()))
            } else {
                emit(Result.failure(e))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.onStart {
        val cached = gamesDao.getAllGames().firstOrNull()
        if (cached != null && cached.isNotEmpty()) {
            emit(Result.success(cached.toDomainFromEntity()))
        }
    }.catch { e ->
        emit(Result.failure(e))
    }

    override suspend fun getGameDetails(id: Int): Result<Game> {
        return try {
            val cachedEntity = gamesDao.getGameById(id)
            if (cachedEntity != null) {
                val cachedGame = cachedEntity.toDomain()

                try {
                    val genresMap = getGenresMap()
                    val response = gamesDbApi.getGameById(id = id.toString())
                    val freshGame = response.toDomain(genresMap)

                    if (freshGame != null) {
                        val gameWithWishlist = freshGame.copy(
                            isInWishlist = cachedGame.isInWishlist,
                            addedToWishlistAt = cachedGame.addedToWishlistAt
                        )

                        gamesDao.insertGame(gameWithWishlist.toEntity())

                        return Result.success(gameWithWishlist)
                    }
                    return Result.success(cachedGame)
                } catch (e: IOException) {
                    return Result.success(cachedGame)
                }
            }

            val genresMap = getGenresMap()
            val response = gamesDbApi.getGameById(id = id.toString())
            val game = response.toDomain(genresMap)
                ?: return Result.failure(Exception("Game not found"))

            gamesDao.insertGame(game.toEntity())

            Result.success(game)

        } catch (e: IOException) {
            Result.failure(IOException("Network error: ${e.message}", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchGames(query: String): Result<List<Game>> {
        return searchGames(query, null, null)
    }

    override suspend fun searchGames(
        query: String,
        genreId: Int?,
        ordering: String?
    ): Result<List<Game>> {
        return try {
            val genresMap = getGenresMap()
            val response = gamesDbApi.getGamesByName(name = query)

            var games = response.toDomainList(genresMap)

            if (genreId != null) {
                val genreName = genresMap[genreId]
                if (genreName != null) {
                    games = games.filter { it.genres.contains(genreName) }
                }
            }

            games = when (ordering) {
                "-rating" -> games.sortedByDescending { it.rating }
                "-released" -> games.sortedByDescending { it.released }
                "name" -> games.sortedBy { it.name }
                else -> games
            }

            games.forEach { game ->
                val existingEntity = gamesDao.getGameById(game.id)
                val isInWishlist = existingEntity?.isInWishlist ?: false
                val addedToWishlistAt = existingEntity?.addedToWishlistAt

                val gameWithWishlist = game.copy(
                    isInWishlist = isInWishlist,
                    addedToWishlistAt = addedToWishlistAt
                )
                gamesDao.insertGame(gameWithWishlist.toEntity())
            }

            Result.success(games)

        } catch (e: IOException) {
            try {
                val cached = gamesDao.searchGames(query)
                Result.success(emptyList())
            } catch (cacheError: Exception) {
                Result.failure(IOException("Network error and no cache available: ${e.message}", e))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getWishlistGames(): Flow<List<Game>> {
        return gamesDao.getWishlistGames()
            .map { it.toDomainFromEntity() }
    }

    override fun getWishlistGamesSorted(sortBy: WishlistSortOption): Flow<List<Game>> {
        return when (sortBy) {
            WishlistSortOption.DATE_ADDED -> {
                gamesDao.getWishlistGamesSortedByDate()
                    .map { it.toDomainFromEntity() }
            }
            WishlistSortOption.RATING -> {
                gamesDao.getWishlistGamesSortedByRating()
                    .map { it.toDomainFromEntity() }
            }
            WishlistSortOption.NAME -> {
                gamesDao.getWishlistGamesSortedByName()
                    .map { it.toDomainFromEntity() }
            }
        }
    }

    override suspend fun addToWishlist(game: Game): Result<Unit> {
        return try {
            val timestamp = System.currentTimeMillis()
            gamesDao.updateWishlistStatus(game.id, inWishlist = true, timestamp)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromWishlist(gameId: Int): Result<Unit> {
        return try {
            gamesDao.updateWishlistStatus(gameId, inWishlist = false, null)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isInWishlist(gameId: Int): Boolean {
        return try {
            val entity = gamesDao.getGameById(gameId)
            entity?.isInWishlist ?: false
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getGenres(): Result<List<Genre>> {
        return try {
            val response = gamesDbApi.getGenres()
            val genres = response.data?.genres?.map { (id, genreDto) ->
                Genre(
                    id = genreDto.id,
                    name = genreDto.name,
                    slug = genreDto.name.lowercase().replace(" ", "-")
                )
            } ?: emptyList()
            Result.success(genres)
        } catch (e: IOException) {
            Result.failure(IOException("Network error: ${e.message}", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGameScreenshots(gameId: Int): Result<List<String>> {
        return try {
            val response = gamesDbApi.getGameImages(gamesId = gameId.toString())
            val baseUrl = response.data?.baseUrl?.medium
                ?: response.data?.baseUrl?.original 
                ?: ""
            
            val screenshots = response.data?.images?.get(gameId.toString())
                ?.filter { it.type == "screenshot" || it.type == "fanart" || it.type == "boxart" }
                ?.mapNotNull { image ->
                    image.filename?.let { filename ->
                        "$baseUrl$filename"
                    }
                } ?: emptyList()
            
            Result.success(screenshots)
        } catch (e: IOException) {
            Result.failure(IOException("Network error: ${e.message}", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSimilarGames(gameId: Int): Result<List<Game>> {
        return try {
            val gameDetails = getGameDetails(gameId).getOrNull()

            if (gameDetails == null || gameDetails.genres.isEmpty()) {
                return Result.success(emptyList())
            }

            val genresMap = getGenresMap()
            val genreName = gameDetails.genres.firstOrNull() ?: return Result.success(emptyList())
            
            val response = gamesDbApi.getGamesByName(name = genreName)
            val games = response.toDomainList(genresMap)
                .filter { it.id != gameId }
                .take(10)
            
            Result.success(games)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
