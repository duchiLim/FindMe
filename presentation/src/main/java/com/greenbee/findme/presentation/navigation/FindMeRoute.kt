package com.greenbee.findme.presentation.navigation

object FindMeRoute {
    const val HOME = "home"
    const val FAVORITE = "favorite"
    const val SHELTER = "shelter"
    const val MY = "my"

    const val ANIMAL_DETAIL = "animal/{desertionNo}"

    val topLevelRoutes = setOf(HOME, FAVORITE, SHELTER, MY)

    fun animalDetail(desertionNo: String): String = "animal/$desertionNo"
}
