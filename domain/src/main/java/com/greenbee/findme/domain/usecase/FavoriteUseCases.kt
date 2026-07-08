package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.model.FAVORITE_NOTICE_UNAVAILABLE_LABEL
import com.greenbee.findme.domain.model.FavoriteAnimal
import com.greenbee.findme.domain.repository.AbandonedAnimalRepository
import com.greenbee.findme.domain.repository.FavoriteRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

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
    suspend operator fun invoke(desertionNos: List<String>): FavoriteRefreshResult {
        if (desertionNos.isEmpty()) return FavoriteRefreshResult()

        var failedCount = 0
        desertionNos.forEach { desertionNo ->
            val animal = runCatching { getAbandonedAnimalDetailUseCase(desertionNo) }.getOrElse {
                failedCount++
                return@forEach
            }
            if (animal == null) {
                favoriteRepository.markNoticeUnavailable(desertionNo)
            } else {
                favoriteRepository.updateFavoriteSnapshot(animal)
            }
        }
        return FavoriteRefreshResult(failedCount = failedCount)
    }
}

data class FavoriteRefreshResult(
    val failedCount: Int = 0,
)

class RestoreFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) {
    suspend operator fun invoke(favorite: FavoriteAnimal) {
        favoriteRepository.addFavorite(favorite.animal, favorite.savedAt)
    }
}

class RemoveEndedFavoritesUseCase @Inject constructor(
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
) {
    suspend operator fun invoke(): Int {
        val favorites = observeFavoritesUseCase().first()
        val endedDesertionNos = favorites
            .filter { isEndedFavorite(it.animal) }
            .map { it.desertionNo }
        endedDesertionNos.forEach { removeFavoriteUseCase(it) }
        return endedDesertionNos.size
    }
}

internal fun isEndedFavorite(animal: AbandonedAnimal): Boolean {
    if (animal.processState == FAVORITE_NOTICE_UNAVAILABLE_LABEL) return true
    val normalized = animal.processState.lowercase()
    return normalized.contains("종료") ||
        normalized.contains("반환") ||
        normalized.contains("자연사")
}
