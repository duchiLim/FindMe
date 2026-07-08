package com.greenbee.findme.data.repository

import com.greenbee.findme.data.local.favorite.FavoriteShelterDao
import com.greenbee.findme.data.local.favorite.FavoriteShelterEntity
import com.greenbee.findme.domain.model.FavoriteShelter
import com.greenbee.findme.domain.model.Shelter
import com.greenbee.findme.domain.repository.ShelterFavoriteRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShelterFavoriteRepositoryImpl @Inject constructor(
    private val favoriteShelterDao: FavoriteShelterDao,
) : ShelterFavoriteRepository {

    override fun observeFavorites(): Flow<List<FavoriteShelter>> =
        favoriteShelterDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeFavoriteCareRegNos(): Flow<Set<String>> =
        favoriteShelterDao.observeCareRegNos().map { it.toSet() }

    override fun observeIsFavorite(careRegNo: String): Flow<Boolean> =
        favoriteShelterDao.observeIsFavorite(careRegNo)

    override suspend fun isFavorite(careRegNo: String): Boolean =
        favoriteShelterDao.isFavorite(careRegNo)

    override suspend fun addFavorite(shelter: Shelter) {
        favoriteShelterDao.insert(
            FavoriteShelterEntity(
                careRegNo = shelter.careRegNo,
                name = shelter.name,
                orgName = shelter.orgName,
                tel = shelter.tel,
                address = shelter.address,
                savedAt = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun removeFavorite(careRegNo: String) {
        favoriteShelterDao.delete(careRegNo)
    }

    private fun FavoriteShelterEntity.toDomain(): FavoriteShelter = FavoriteShelter(
        careRegNo = careRegNo,
        name = name,
        orgName = orgName,
        tel = tel,
        address = address,
        savedAt = savedAt,
    )
}
