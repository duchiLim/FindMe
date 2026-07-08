package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.RecentlyViewedAnimal
import com.greenbee.findme.domain.repository.AbandonedAnimalRepository
import com.greenbee.findme.domain.repository.RecentlyViewedRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveRecentlyViewedUseCase @Inject constructor(
    private val repository: RecentlyViewedRepository,
) {
    operator fun invoke(
        limit: Int = RecentlyViewedRepository.DEFAULT_PREVIEW_LIMIT,
    ): Flow<List<RecentlyViewedAnimal>> = repository.observeRecentlyViewed(limit)
}

class RecordRecentlyViewedUseCase @Inject constructor(
    private val recentlyViewedRepository: RecentlyViewedRepository,
    private val abandonedAnimalRepository: AbandonedAnimalRepository,
) {
    suspend operator fun invoke(desertionNo: String) {
        val animal = abandonedAnimalRepository.getCachedAbandonedAnimal(desertionNo)
            ?: abandonedAnimalRepository.getAbandonedAnimal(desertionNo)
            ?: return
        recentlyViewedRepository.recordView(animal)
    }
}
