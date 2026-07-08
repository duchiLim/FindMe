package com.greenbee.findme.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.greenbee.findme.presentation.detail.AnimalDetailScreen
import com.greenbee.findme.presentation.favorite.FavoriteScreen
import com.greenbee.findme.presentation.home.HomeScreen
import com.greenbee.findme.presentation.home.today.HomeTodayRegisteredScreen
import com.greenbee.findme.presentation.my.MyAboutScreen
import com.greenbee.findme.presentation.my.MyApplicationsScreen
import com.greenbee.findme.presentation.my.MyRecentScreen
import com.greenbee.findme.presentation.my.MyScreen
import com.greenbee.findme.presentation.shelter.ShelterDetailScreen
import com.greenbee.findme.presentation.shelter.ShelterScreen
import com.greenbee.findme.presentation.shelter.animals.ShelterAnimalsScreen

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
                onNavigateToFavorite = {
                    navController.navigate(FindMeRoute.FAVORITE) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToRecent = {
                    navController.navigate(FindMeRoute.MY_RECENT)
                },
                onNavigateToTodayRegistered = {
                    navController.navigate(FindMeRoute.HOME_TODAY)
                },
            )
        }
        composable(FindMeRoute.HOME_TODAY) {
            HomeTodayRegisteredScreen(
                onBackClick = { navController.popBackStack() },
                onAnimalClick = { desertionNo ->
                    navController.navigate(FindMeRoute.animalDetail(desertionNo))
                },
                onNavigateToFavorite = {
                    navController.navigate(FindMeRoute.FAVORITE) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
        composable(FindMeRoute.FAVORITE) {
            FavoriteScreen(
                onAnimalClick = { desertionNo ->
                    navController.navigate(FindMeRoute.animalDetail(desertionNo))
                },
                onBrowseAnimalsClick = {
                    navController.navigate(FindMeRoute.HOME) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
        composable(FindMeRoute.SHELTER) {
            ShelterScreen(
                onShelterClick = { careRegNo ->
                    navController.navigate(FindMeRoute.shelterDetail(careRegNo))
                },
            )
        }
        composable(
            route = FindMeRoute.SHELTER_DETAIL,
            arguments = listOf(
                navArgument("careRegNo") { type = NavType.StringType },
            ),
        ) {
            ShelterDetailScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToAnimals = { careRegNo, shelterName ->
                    navController.navigate(FindMeRoute.shelterAnimals(careRegNo, shelterName))
                },
            )
        }
        composable(
            route = FindMeRoute.SHELTER_ANIMALS,
            arguments = listOf(
                navArgument("careRegNo") { type = NavType.StringType },
                navArgument("shelterName") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) {
            ShelterAnimalsScreen(
                onBackClick = { navController.popBackStack() },
                onAnimalClick = { desertionNo ->
                    navController.navigate(FindMeRoute.animalDetail(desertionNo))
                },
            )
        }
        composable(FindMeRoute.MY) {
            MyScreen(
                onAnimalClick = { desertionNo ->
                    navController.navigate(FindMeRoute.animalDetail(desertionNo))
                },
                onNavigateToRecent = {
                    navController.navigate(FindMeRoute.MY_RECENT)
                },
                onNavigateToAbout = {
                    navController.navigate(FindMeRoute.MY_ABOUT)
                },
                onNavigateToApplications = {
                    navController.navigate(FindMeRoute.MY_APPLICATIONS)
                },
                onNavigateToFavorite = {
                    navController.navigate(FindMeRoute.FAVORITE) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
        composable(FindMeRoute.MY_ABOUT) {
            MyAboutScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
        composable(FindMeRoute.MY_RECENT) {
            MyRecentScreen(
                onBackClick = { navController.popBackStack() },
                onAnimalClick = { desertionNo ->
                    navController.navigate(FindMeRoute.animalDetail(desertionNo))
                },
            )
        }
        composable(FindMeRoute.MY_APPLICATIONS) {
            MyApplicationsScreen(
                onBackClick = { navController.popBackStack() },
                onBrowseHomeClick = {
                    navController.navigate(FindMeRoute.HOME) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBrowseFavoriteClick = {
                    navController.navigate(FindMeRoute.FAVORITE) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
        composable(
            route = FindMeRoute.ANIMAL_DETAIL,
            arguments = listOf(
                navArgument("desertionNo") { type = NavType.StringType },
            ),
        ) {
            AnimalDetailScreen(
                onBackClick = { navController.popBackStack() },
                onShelterClick = { careRegNo ->
                    navController.navigate(FindMeRoute.shelterDetail(careRegNo))
                },
            )
        }
    }
}
