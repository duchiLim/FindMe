package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.Shelter
import com.greenbee.findme.domain.repository.ShelterRepository
import javax.inject.Inject

class GetShelterDetailUseCase @Inject constructor(
    private val repository: ShelterRepository,
) {
    suspend operator fun invoke(careRegNo: String): Shelter? =
        repository.getShelter(careRegNo)
}

class FindShelterByNameUseCase @Inject constructor(
    private val repository: ShelterRepository,
) {
    suspend operator fun invoke(name: String): Shelter? =
        repository.findShelterByName(name)
}
