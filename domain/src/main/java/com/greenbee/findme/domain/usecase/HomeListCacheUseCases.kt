package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.model.HomeListCache
import com.greenbee.findme.domain.repository.HomeListCacheRepository
import javax.inject.Inject

class GetHomeListCacheUseCase @Inject constructor(
    private val repository: HomeListCacheRepository,
) {
    suspend operator fun invoke(): HomeListCache? = repository.getCache()
}

class SaveHomeListCacheUseCase @Inject constructor(
    private val repository: HomeListCacheRepository,
) {
    suspend operator fun invoke(
        animals: List<AbandonedAnimal>,
        totalCount: Int,
        filterSummary: String,
    ) {
        repository.saveCache(
            animals = animals,
            totalCount = totalCount,
            filterSummary = filterSummary,
        )
    }
}
