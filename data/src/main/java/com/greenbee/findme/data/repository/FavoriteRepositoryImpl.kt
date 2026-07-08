package com.greenbee.findme.data.repository

import com.greenbee.findme.data.local.favorite.FavoriteAnimalDao
import com.greenbee.findme.data.mapper.toDomain
import com.greenbee.findme.data.mapper.toFavoriteEntity
import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.model.FavoriteAnimal
import com.greenbee.findme.domain.repository.FavoriteRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteAnimalDao: FavoriteAnimalDao,
) : FavoriteRepository {

    override fun observeFavorites(): Flow<List<FavoriteAnimal>> =
        favoriteAnimalDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeIsFavorite(desertionNo: String): Flow<Boolean> =
        favoriteAnimalDao.observeIsFavorite(desertionNo)

    override suspend fun isFavorite(desertionNo: String): Boolean =
        favoriteAnimalDao.isFavorite(desertionNo)

    override suspend fun addFavorite(animal: AbandonedAnimal) {
        val existing = favoriteAnimalDao.getByDesertionNo(animal.desertionNo)
        if (existing == null) {
            favoriteAnimalDao.insert(animal.toFavoriteEntity())
        } else {
            favoriteAnimalDao.update(animal.toFavoriteEntity(savedAt = existing.savedAt))
        }
    }

    override suspend fun removeFavorite(desertionNo: String) {
        favoriteAnimalDao.delete(desertionNo)
    }

    override suspend fun updateFavoriteSnapshot(animal: AbandonedAnimal) {
        val existing = favoriteAnimalDao.getByDesertionNo(animal.desertionNo) ?: return
        favoriteAnimalDao.update(animal.toFavoriteEntity(savedAt = existing.savedAt))
    }
}
