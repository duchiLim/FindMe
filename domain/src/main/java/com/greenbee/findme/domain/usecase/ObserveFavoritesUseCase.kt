package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.FavoriteAnimal
import com.greenbee.findme.domain.repository.FavoriteRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveFavoritesUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    operator fun invoke(): Flow<List<FavoriteAnimal>> = repository.observeFavorites()
}
