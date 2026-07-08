package com.greenbee.findme.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.greenbee.findme.presentation.R
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.greenbee.findme.presentation.common.PlaceholderScreen
import com.greenbee.findme.presentation.detail.AnimalDetailScreen
import com.greenbee.findme.presentation.home.HomeScreen
import com.greenbee.findme.presentation.shelter.ShelterScreen

@Composable
fun FindMeNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = FindMeRoute.HOME,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(FindMeRoute.HOME) {
            HomeScreen(
                onAnimalClick = { desertionNo ->
                    navController.navigate(FindMeRoute.animalDetail(desertionNo))
                },
            )
        }
        composable(FindMeRoute.FAVORITE) {
            PlaceholderScreen(title = stringResource(R.string.favorite_screen_title))
        }
        composable(FindMeRoute.SHELTER) {
            ShelterScreen()
        }
        composable(FindMeRoute.MY) {
            PlaceholderScreen(title = "마이")
        }
        composable(
            route = FindMeRoute.ANIMAL_DETAIL,
            arguments = listOf(
                navArgument("desertionNo") { type = NavType.StringType },
            ),
        ) {
            AnimalDetailScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
