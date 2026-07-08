package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.PagedResult
import com.greenbee.findme.domain.model.Shelter
import com.greenbee.findme.domain.repository.ShelterRepository
import javax.inject.Inject

class GetShelterListUseCase @Inject constructor(
    private val repository: ShelterRepository,
) {
    suspend operator fun invoke(
        sidoCode: String,
        sigunguCode: String,
        pageNo: Int = 1,
        numOfRows: Int = DEFAULT_PAGE_SIZE,
    ): PagedResult<Shelter> = repository.getShelters(
        sidoCode = sidoCode,
        sigunguCode = sigunguCode,
        pageNo = pageNo,
        numOfRows = numOfRows,
    )

    private companion object {
        const val DEFAULT_PAGE_SIZE = 100
    }
}
