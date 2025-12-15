package com.example.finalproject.data.mapper

import com.example.finalproject.data.local.GameEntity
import com.example.finalproject.data.remote.dto.BoxartDataDto
import com.example.finalproject.data.remote.dto.GameDto
import com.example.finalproject.data.remote.dto.GamesByIdResponseDto
import com.example.finalproject.data.remote.dto.GamesByNameResponseDto
import com.example.finalproject.data.remote.dto.GenresDataDto
import com.example.finalproject.data.remote.dto.IncludeDataDto
import com.example.finalproject.domain.model.Game
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GameMapper {
    private val gson = Gson()
    fun GameDto.toDomain(include: IncludeDataDto? = null, genresMap: Map<Int, String>? = null): Game {
        val backgroundImage = getBackgroundImage(id, include?.boxart)
        
        val platformName = include?.platform?.data?.get(platform.toString())?.name
        val platforms = if (platformName != null) listOf(platformName) else emptyList()
        
        val genreNames = genres?.mapNotNull { genreId -> genresMap?.get(genreId) } ?: emptyList()
        
        val ratingFloat = parseRating(rating)
        
        return Game(
            id = id,
            name = gameTitle ?: "Unknown",
            backgroundImage = backgroundImage,
            rating = ratingFloat,
            released = releaseDate,
            genres = genreNames,
            platforms = platforms,
            description = overview,
            metacritic = null,
            isInWishlist = false,
            addedToWishlistAt = null
        )
    }

    private fun getBackgroundImage(gameId: Int, boxart: BoxartDataDto?): String? {
        if (boxart == null) return null
        
        val baseUrl = boxart.baseUrl?.large ?: boxart.baseUrl?.medium ?: boxart.baseUrl?.original ?: ""
        val gameBoxart = boxart.data?.get(gameId.toString())
        
        val frontBoxart = gameBoxart?.find { it.side == "front" }
        val anyBoxart = gameBoxart?.firstOrNull()
        
        val filename = frontBoxart?.filename ?: anyBoxart?.filename
        
        return if (filename != null && baseUrl.isNotEmpty()) {
            "$baseUrl$filename"
        } else null
    }

    private fun parseRating(rating: String?): Float {
        return when {
            rating == null -> 0f
            rating.contains("E - Everyone", ignoreCase = true) -> 4.0f
            rating.contains("E10+", ignoreCase = true) -> 3.8f
            rating.contains("T - Teen", ignoreCase = true) -> 3.5f
            rating.contains("M - Mature", ignoreCase = true) -> 3.0f
            rating.contains("AO", ignoreCase = true) -> 2.5f
            else -> 3.5f
        }
    }

    fun GamesByNameResponseDto.toDomainList(genresMap: Map<Int, String>? = null): List<Game> {
        val games = data?.games ?: return emptyList()
        val includeData = include
        return games.map { it.toDomain(includeData, genresMap) }
    }

    fun GamesByIdResponseDto.toDomain(genresMap: Map<Int, String>? = null): Game? {
        val game = data?.games?.firstOrNull() ?: return null
        return game.toDomain(include, genresMap)
    }

    fun Game.toEntity(): GameEntity {
        return GameEntity(
            id = id,
            name = name,
            backgroundImage = backgroundImage,
            rating = rating,
            released = released,
            genres = listToJson(genres),
            platforms = listToJson(platforms),
            description = description,
            metacritic = metacritic,
            isInWishlist = isInWishlist,
            addedToWishlistAt = addedToWishlistAt,
            cachedAt = System.currentTimeMillis()
        )
    }

    fun GameEntity.toDomain(): Game {
        return Game(
            id = id,
            name = name,
            backgroundImage = backgroundImage,
            rating = rating,
            released = released,
            genres = jsonToList(genres),
            platforms = jsonToList(platforms),
            description = description,
            metacritic = metacritic,
            isInWishlist = isInWishlist,
            addedToWishlistAt = addedToWishlistAt
        )
    }

    fun List<GameEntity>.toDomainFromEntity(): List<Game> {
        return map { it.toDomain() }
    }



    fun GenresDataDto.toGenresMap(): Map<Int, String> {
        val result = mutableMapOf<Int, String>()
        genres?.forEach { (_, genreDto) ->
            result[genreDto.id] = genreDto.name
        }
        return result
    }

    private fun listToJson(list: List<String>): String {
        return gson.toJson(list)
    }

    private fun jsonToList(json: String): List<String> {
        return try {
            val listType = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
