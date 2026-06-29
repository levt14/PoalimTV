package com.lev.poalimtv.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lev.poalimtv.ui.detail.DetailScreen
import com.lev.poalimtv.ui.favorites.FavoritesScreen
import com.lev.poalimtv.ui.home.HomeScreen
import com.lev.poalimtv.ui.search.SearchScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onSearchClick = { navController.navigate("search") },
                onFavoritesClick = { navController.navigate("favorites") },
                onMediaClick = { mediaItem ->
                    navController.navigate("detail/${mediaItem.mediaType.name.lowercase()}/${mediaItem.id}")
                },
            )
        }
        composable(
            route = "detail/{mediaType}/{mediaId}",
            arguments = listOf(
                navArgument("mediaType") { type = NavType.StringType },
                navArgument("mediaId") { type = NavType.IntType },
            ),
        ) {
            DetailScreen()
        }
        dialog(
            route = "search",
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            SearchScreen(
                onDismiss = { navController.popBackStack() },
                onMediaClick = { mediaItem ->
                    navController.popBackStack()
                    navController.navigate("detail/${mediaItem.mediaType.name.lowercase()}/${mediaItem.id}")
                },
            )
        }
        dialog("favorites") {
            FavoritesScreen(onDismiss = { navController.popBackStack() })
        }
    }
}
