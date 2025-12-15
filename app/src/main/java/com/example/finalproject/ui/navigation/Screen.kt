package com.example.finalproject.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Wishlist : Screen("wishlist")
    
    object GameDetails : Screen("details/{gameId}") {
        fun createRoute(gameId: Int) = "details/$gameId"
    }
}
