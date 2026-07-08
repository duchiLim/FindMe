package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.FavoriteShelter
import com.greenbee.findme.domain.model.Shelter
import com.greenbee.findme.domain.repository.ShelterFavoriteRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveShelterFavoritesUseCase @Inject constructor(
    private val repository: ShelterFavoriteRepository,
) {
    operator fun invoke(): Flow<List<FavoriteShelter>> = repository.observeFavorites()
}

class ObserveShelterFavoriteCareRegNosUseCase @Inject constructor(
    private val repository: ShelterFavoriteRepository,
) {
    operator fun invoke(): Flow<Set<String>> = repository.observeFavoriteCareRegNos()
}

class ObserveIsShelterFavoriteUseCase @Inject constructor(
    private val repository: ShelterFavoriteRepository,
) {
    operator fun invoke(careRegNo: String): Flow<Boolean> =
        repository.observeIsFavorite(careRegNo)
}

enum class ShelterFavoriteToggleResult {
    ADDED,
    REMOVED,
}

class ToggleShelterFavoriteUseCase @Inject constructor(
    private val repository: ShelterFavoriteRepository,
) {
    suspend operator fun invoke(shelter: Shelter): ShelterFavoriteToggleResult {
        return if (repository.isFavorite(shelter.careRegNo)) {
            repository.removeFavorite(shelter.careRegNo)
            ShelterFavoriteToggleResult.REMOVED
        } else {
            repository.addFavorite(shelter)
            ShelterFavoriteToggleResult.ADDED
        }
    }
}
