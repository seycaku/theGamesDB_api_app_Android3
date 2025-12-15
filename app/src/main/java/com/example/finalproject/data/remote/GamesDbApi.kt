package com.example.finalproject.data.remote

import com.example.finalproject.data.remote.dto.GamesByIdResponseDto
import com.example.finalproject.data.remote.dto.GamesByNameResponseDto
import com.example.finalproject.data.remote.dto.GamesImagesResponseDto
import com.example.finalproject.data.remote.dto.GenresResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GamesDbApi {

    @GET("v1/Games/ByGameName")
    suspend fun getGamesByName(
        @Query("apikey") apiKey: String = API_KEY,
        @Query("name") name: String,
        @Query("fields") fields: String = DEFAULT_FIELDS,
        @Query("include") include: String = DEFAULT_INCLUDE,
        @Query("page") page: Int = 1
    ): GamesByNameResponseDto

    @GET("v1/Games/ByGameID")
    suspend fun getGameById(
        @Query("apikey") apiKey: String = API_KEY,
        @Query("id") id: String,
        @Query("fields") fields: String = DEFAULT_FIELDS,
        @Query("include") include: String = DEFAULT_INCLUDE
    ): GamesByIdResponseDto

    @GET("v1/Games/Images")
    suspend fun getGameImages(
        @Query("apikey") apiKey: String = API_KEY,
        @Query("games_id") gamesId: String,
        @Query("filter[type]") filterType: String = "fanart, banner, boxart, screenshot, clearlogo, titlescreen"
    ): GamesImagesResponseDto

    @GET("v1/Genres")
    suspend fun getGenres(
        @Query("apikey") apiKey: String = API_KEY
    ): GenresResponseDto

    companion object {
        const val BASE_URL = "https://api.thegamesdb.net/"
        const val API_KEY = "4389c1804707ceaa5ac3a69622b5654d538987960342227568a4b6f5a5ae824e"
        const val DEFAULT_FIELDS = "players,publishers,genres,overview,rating,platform"
        const val DEFAULT_INCLUDE = "boxart,platform"
    }
}
