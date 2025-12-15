package com.example.finalproject.data.remote.dto

data class GenreDto(
    val id: Int,
    val name: String
)

data class GenresResponseDto(
    val code: Int?,
    val status: String?,
    val data: GenresDataDto?
)

data class GenresDataDto(
    val count: Int?,
    val genres: Map<String, GenreDto>?
)
