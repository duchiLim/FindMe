package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.repository.AbandonedAnimalRepository
import javax.inject.Inject

class GetAbandonedAnimalDetailUseCase @Inject constructor(
    private val repository: AbandonedAnimalRepository,
) {
    fun getCached(desertionNo: String): AbandonedAnimal? =
        repository.getCachedAbandonedAnimal(desertionNo)

    suspend operator fun invoke(desertionNo: String): AbandonedAnimal? =
        repository.getAbandonedAnimal(desertionNo)
}
