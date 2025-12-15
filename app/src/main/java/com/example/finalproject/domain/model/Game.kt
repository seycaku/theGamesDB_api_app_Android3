package com.example.finalproject.domain.model

data class Game(
    val id: Int,
    val name: String,
    val backgroundImage: String?,
    val rating: Float,
    val released: String?,
    val genres: List<String>,
    val platforms: List<String>,
    val description: String?,
    val metacritic: Int?,
    val isInWishlist: Boolean = false,
    val addedToWishlistAt: Long? = null
)

data class Genre(
    val id: Int,
    val name: String,
    val slug: String
)
