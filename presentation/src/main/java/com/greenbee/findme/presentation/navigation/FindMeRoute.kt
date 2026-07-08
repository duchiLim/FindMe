package com.greenbee.findme.presentation.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object FindMeRoute {
    const val HOME = "home"
    const val FAVORITE = "favorite"
    const val SHELTER = "shelter"
    const val MY = "my"

    const val ANIMAL_DETAIL = "animal/{desertionNo}"
    const val SHELTER_DETAIL = "shelter/{careRegNo}"
    const val SHELTER_ANIMALS = "shelter/{careRegNo}/animals?shelterName={shelterName}"
    const val MY_ABOUT = "my/about"
    const val MY_RECENT = "my/recent"
    const val MY_APPLICATIONS = "my/applications"
    const val HOME_TODAY = "home/today"

    val topLevelRoutes = setOf(HOME, FAVORITE, SHELTER, MY)

    fun animalDetail(desertionNo: String): String = "animal/$desertionNo"

    fun shelterDetail(careRegNo: String): String = "shelter/$careRegNo"

    fun shelterAnimals(careRegNo: String, shelterName: String): String {
        val encodedName = URLEncoder.encode(shelterName, StandardCharsets.UTF_8.toString())
        return "shelter/$careRegNo/animals?shelterName=$encodedName"
    }
}
