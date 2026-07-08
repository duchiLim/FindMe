package com.greenbee.findme.domain.repository

import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.model.FavoriteAnimal
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<List<FavoriteAnimal>>

    fun observeIsFavorite(desertionNo: String): Flow<Boolean>

    suspend fun isFavorite(desertionNo: String): Boolean

    suspend fun addFavorite(animal: AbandonedAnimal)

    suspend fun removeFavorite(desertionNo: String)

    suspend fun updateFavoriteSnapshot(animal: AbandonedAnimal)
}
