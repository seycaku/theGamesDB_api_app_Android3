package com.example.finalproject.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val backgroundImage: String?,
    val rating: Float,
    val released: String?,
    val genres: String,
    val platforms: String,
    val description: String?,
    val metacritic: Int?,
    val isInWishlist: Boolean = false,
    val addedToWishlistAt: Long? = null,
    val cachedAt: Long = System.currentTimeMillis()
)
