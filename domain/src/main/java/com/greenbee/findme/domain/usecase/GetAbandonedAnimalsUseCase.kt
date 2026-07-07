package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.model.AbandonedAnimalFilter
import com.greenbee.findme.domain.model.PagedResult
import com.greenbee.findme.domain.repository.AbandonedAnimalRepository
import javax.inject.Inject

class GetAbandonedAnimalsUseCase @Inject constructor(
    private val repository: AbandonedAnimalRepository,
) {
    suspend operator fun invoke(
        filter: AbandonedAnimalFilter = AbandonedAnimalFilter(),
    ): PagedResult<AbandonedAnimal> = repository.getAbandonedAnimals(filter)
}
