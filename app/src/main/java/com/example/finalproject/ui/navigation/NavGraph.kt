package com.example.finalproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.finalproject.ui.screens.details.GameDetailsScreen
import com.example.finalproject.ui.screens.home.HomeScreen
import com.example.finalproject.ui.screens.search.SearchScreen
import com.example.finalproject.ui.screens.wishlist.WishlistScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onGameClick = { gameId ->
                    navController.navigate(Screen.GameDetails.createRoute(gameId))
                }
            )
        }
        
        composable(Screen.Search.route) {
            SearchScreen(
                onGameClick = { gameId ->
                    navController.navigate(Screen.GameDetails.createRoute(gameId))
                }
            )
        }
        
        composable(Screen.Wishlist.route) {
            WishlistScreen(
                onGameClick = { gameId ->
                    navController.navigate(Screen.GameDetails.createRoute(gameId))
                }
            )
        }
        
        composable(
            route = Screen.GameDetails.route,
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getInt("gameId")
            if (gameId != null) {
                GameDetailsScreen(
                    onGameClick = { id ->
                        navController.navigate(Screen.GameDetails.createRoute(id))
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
