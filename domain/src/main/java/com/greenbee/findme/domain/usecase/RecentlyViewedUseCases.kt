package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.RecentlyViewedAnimal
import com.greenbee.findme.domain.repository.AbandonedAnimalRepository
import com.greenbee.findme.domain.repository.RecentlyViewedRepository
import com.greenbee.findme.domain.repository.UserSettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveRecentlyViewedUseCase @Inject constructor(
    private val repository: RecentlyViewedRepository,
) {
    operator fun invoke(
        limit: Int = RecentlyViewedRepository.DEFAULT_PREVIEW_LIMIT,
    ): Flow<List<RecentlyViewedAnimal>> = repository.observeRecentlyViewed(limit)
}

class ObserveAllRecentlyViewedUseCase @Inject constructor(
    private val repository: RecentlyViewedRepository,
) {
    operator fun invoke(): Flow<List<RecentlyViewedAnimal>> =
        repository.observeAllRecentlyViewed()
}

class RecordRecentlyViewedUseCase @Inject constructor(
    private val recentlyViewedRepository: RecentlyViewedRepository,
    private val abandonedAnimalRepository: AbandonedAnimalRepository,
    private val userSettingsRepository: UserSettingsRepository,
) {
    suspend operator fun invoke(desertionNo: String) {
        if (!userSettingsRepository.isRecentViewingEnabled()) return

        val animal = abandonedAnimalRepository.getCachedAbandonedAnimal(desertionNo)
            ?: abandonedAnimalRepository.getAbandonedAnimal(desertionNo)
            ?: return
        recentlyViewedRepository.recordView(animal)
    }
}

class ClearRecentlyViewedUseCase @Inject constructor(
    private val repository: RecentlyViewedRepository,
) {
    suspend operator fun invoke() {
        repository.clearAll()
    }
}

class RemoveRecentlyViewedUseCase @Inject constructor(
    private val repository: RecentlyViewedRepository,
) {
    suspend operator fun invoke(desertionNo: String) {
        repository.removeRecentlyViewed(desertionNo)
    }
}

class RefreshRecentSnapshotsUseCase @Inject constructor(
    private val getAbandonedAnimalDetailUseCase: GetAbandonedAnimalDetailUseCase,
    private val recentlyViewedRepository: RecentlyViewedRepository,
) {
    suspend operator fun invoke(desertionNos: List<String>) {
        desertionNos.forEach { desertionNo ->
            runCatching { getAbandonedAnimalDetailUseCase(desertionNo) }
                .getOrNull()
                ?.let { recentlyViewedRepository.updateSnapshot(it) }
        }
    }
}
