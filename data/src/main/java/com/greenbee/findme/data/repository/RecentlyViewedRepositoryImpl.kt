package com.greenbee.findme.data.repository

import com.greenbee.findme.data.local.recent.RecentlyViewedDao
import com.greenbee.findme.data.mapper.toDomain
import com.greenbee.findme.data.mapper.toRecentlyViewedEntity
import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.model.RecentlyViewedAnimal
import com.greenbee.findme.domain.repository.RecentlyViewedRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecentlyViewedRepositoryImpl @Inject constructor(
    private val recentlyViewedDao: RecentlyViewedDao,
) : RecentlyViewedRepository {

    override fun observeRecentlyViewed(limit: Int): Flow<List<RecentlyViewedAnimal>> =
        recentlyViewedDao.observeRecent(limit).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeAllRecentlyViewed(): Flow<List<RecentlyViewedAnimal>> =
        recentlyViewedDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun recordView(animal: AbandonedAnimal) {
        val now = System.currentTimeMillis()
        val existing = recentlyViewedDao.getByDesertionNo(animal.desertionNo)
        val entity = animal.toRecentlyViewedEntity(viewedAt = now)
        if (existing == null) {
            recentlyViewedDao.insert(entity)
        } else {
            recentlyViewedDao.update(entity)
        }
        trimToMax()
    }

    override suspend fun updateSnapshot(animal: AbandonedAnimal) {
        val existing = recentlyViewedDao.getByDesertionNo(animal.desertionNo) ?: return
        recentlyViewedDao.update(
            animal.toRecentlyViewedEntity(viewedAt = existing.viewedAt),
        )
    }

    override suspend fun removeRecentlyViewed(desertionNo: String) {
        recentlyViewedDao.delete(desertionNo)
    }

    override suspend fun clearAll() {
        recentlyViewedDao.deleteAll()
    }

    private suspend fun trimToMax() {
        val count = recentlyViewedDao.count()
        val overflow = count - RecentlyViewedRepository.MAX_STORED_COUNT
        if (overflow > 0) {
            recentlyViewedDao.deleteOldest(overflow)
        }
    }
}
