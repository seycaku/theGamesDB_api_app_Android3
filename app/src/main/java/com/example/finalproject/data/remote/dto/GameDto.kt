package com.example.finalproject.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GamesByNameResponseDto(
    val code: Int?,
    val status: String?,
    val data: GamesDataDto?,
    val include: IncludeDataDto?,
    val pages: PagesDto?
)

data class GamesByIdResponseDto(
    val code: Int?,
    val status: String?,
    val data: GamesDataDto?,
    val include: IncludeDataDto?
)

data class GamesDataDto(
    val count: Int?,
    val games: List<GameDto>?
)

data class PagesDto(
    val previous: String?,
    val current: String?,
    val next: String?
)

data class GameDto(
    val id: Int,
    @SerializedName("game_title")
    val gameTitle: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    val platform: Int?,
    val players: Int?,
    val overview: String?,
    val rating: String?,
    val genres: List<Int>?,
    val publishers: List<Int>?,
    val developers: List<Int>?
)

data class IncludeDataDto(
    val boxart: BoxartDataDto?,
    val platform: PlatformDataDto?
)

data class BoxartDataDto(
    @SerializedName("base_url")
    val baseUrl: BaseUrlDto?,
    val data: Map<String, List<BoxartItemDto>>?
)

data class BaseUrlDto(
    val original: String?,
    val small: String?,
    val thumb: String?,
    val cropped_center_thumb: String?,
    val medium: String?,
    val large: String?
)

data class BoxartItemDto(
    val id: Int?,
    val type: String?,
    val side: String?,
    val filename: String?,
    val resolution: String?
)

data class PlatformDataDto(
    val data: Map<String, PlatformInfoDto>?
)

data class PlatformInfoDto(
    val id: Int?,
    val name: String?,
    val alias: String?
)

data class GamesImagesResponseDto(
    val code: Int?,
    val status: String?,
    val data: ImagesDataDto?
)

data class ImagesDataDto(
    @SerializedName("base_url")
    val baseUrl: BaseUrlDto?,
    val images: Map<String, List<ImageItemDto>>?
)

data class ImageItemDto(
    val id: Int?,
    val type: String?,
    val side: String?,
    val filename: String?,
    val resolution: String?
)

