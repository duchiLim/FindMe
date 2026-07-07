package com.greenbee.findme.domain.repository

import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.model.AbandonedAnimalFilter
import com.greenbee.findme.domain.model.PagedResult

interface AbandonedAnimalRepository {
    suspend fun getAbandonedAnimals(filter: AbandonedAnimalFilter): PagedResult<AbandonedAnimal>
}
