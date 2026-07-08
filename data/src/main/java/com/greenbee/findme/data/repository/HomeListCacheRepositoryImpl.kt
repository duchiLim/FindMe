package com.greenbee.findme.data.repository

import com.greenbee.findme.data.settings.HomeListCacheDataSource
import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.model.HomeListCache
import com.greenbee.findme.domain.repository.HomeListCacheRepository
import javax.inject.Inject

class HomeListCacheRepositoryImpl @Inject constructor(
    private val dataSource: HomeListCacheDataSource,
) : HomeListCacheRepository {

    override suspend fun getCache(): HomeListCache? = dataSource.load()

    override suspend fun saveCache(
        animals: List<AbandonedAnimal>,
        totalCount: Int,
        filterSummary: String,
    ) {
        dataSource.save(
            animals = animals,
            totalCount = totalCount,
            filterSummary = filterSummary,
        )
    }

    override suspend fun clearCache() {
        dataSource.clear()
    }
}
