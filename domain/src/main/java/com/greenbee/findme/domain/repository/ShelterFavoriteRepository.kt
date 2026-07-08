package com.greenbee.findme.domain.repository

import com.greenbee.findme.domain.model.FavoriteShelter
import com.greenbee.findme.domain.model.Shelter
import kotlinx.coroutines.flow.Flow

interface ShelterFavoriteRepository {
    fun observeFavorites(): Flow<List<FavoriteShelter>>

    fun observeFavoriteCareRegNos(): Flow<Set<String>>

    fun observeIsFavorite(careRegNo: String): Flow<Boolean>

    suspend fun isFavorite(careRegNo: String): Boolean

    suspend fun addFavorite(shelter: Shelter)

    suspend fun removeFavorite(careRegNo: String)
}
