package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.PagedResult
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.domain.repository.RegionRepository
import javax.inject.Inject

class GetSigunguListUseCase @Inject constructor(
    private val repository: RegionRepository,
) {
    suspend operator fun invoke(sidoCode: String): PagedResult<Region> =
        repository.getSigunguList(sidoCode = sidoCode)
}
