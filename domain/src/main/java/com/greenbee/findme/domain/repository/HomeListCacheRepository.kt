package com.greenbee.findme.domain.repository

import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.model.HomeListCache

interface HomeListCacheRepository {
    suspend fun getCache(): HomeListCache?

    suspend fun saveCache(
        animals: List<AbandonedAnimal>,
        totalCount: Int,
        filterSummary: String,
    )

    suspend fun clearCache()
}
