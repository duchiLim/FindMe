package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.repository.AbandonedAnimalRepository
import com.greenbee.findme.domain.repository.FavoriteRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveIsFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    operator fun invoke(desertionNo: String): Flow<Boolean> =
        repository.observeIsFavorite(desertionNo)
}

class AddFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val abandonedAnimalRepository: AbandonedAnimalRepository,
) {
    suspend operator fun invoke(desertionNo: String) {
        val animal = abandonedAnimalRepository.getCachedAbandonedAnimal(desertionNo)
            ?: abandonedAnimalRepository.getAbandonedAnimal(desertionNo)
            ?: error("동물 정보를 찾을 수 없습니다.")
        favoriteRepository.addFavorite(animal)
    }
}

class RemoveFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    suspend operator fun invoke(desertionNo: String) {
        repository.removeFavorite(desertionNo)
    }
}

enum class FavoriteToggleResult {
    ADDED,
    REMOVED,
}

class ToggleFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
) {
    suspend operator fun invoke(desertionNo: String): FavoriteToggleResult {
        return if (favoriteRepository.isFavorite(desertionNo)) {
            removeFavoriteUseCase(desertionNo)
            FavoriteToggleResult.REMOVED
        } else {
            addFavoriteUseCase(desertionNo)
            FavoriteToggleResult.ADDED
        }
    }
}

class RefreshFavoriteSnapshotsUseCase @Inject constructor(
    private val getAbandonedAnimalDetailUseCase: GetAbandonedAnimalDetailUseCase,
    private val favoriteRepository: FavoriteRepository,
) {
    suspend operator fun invoke(desertionNos: List<String>) {
        desertionNos.forEach { desertionNo ->
            runCatching { getAbandonedAnimalDetailUseCase(desertionNo) }
                .getOrNull()
                ?.let { favoriteRepository.updateFavoriteSnapshot(it) }
        }
    }
}
