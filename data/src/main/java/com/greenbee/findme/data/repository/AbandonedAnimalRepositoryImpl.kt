package com.greenbee.findme.data.repository

import com.greenbee.findme.data.mapper.toDomain
import com.greenbee.findme.data.mapper.toPagedResult
import com.greenbee.findme.data.remote.AbandonmentPublicApi
import com.greenbee.findme.data.remote.AbandonmentPublicRemoteDataSource
import com.greenbee.findme.data.util.withDefaultDateRange
import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.model.AbandonedAnimalFilter
import com.greenbee.findme.domain.model.PagedResult
import com.greenbee.findme.domain.repository.AbandonedAnimalRepository
import javax.inject.Inject

class AbandonedAnimalRepositoryImpl @Inject constructor(
    private val remoteDataSource: AbandonmentPublicRemoteDataSource,
    private val api: AbandonmentPublicApi,
) : AbandonedAnimalRepository {

    override suspend fun getAbandonedAnimals(
        filter: AbandonedAnimalFilter,
    ): PagedResult<AbandonedAnimal> {
        val filter = filter.withDefaultDateRange()
        val body = remoteDataSource.fetch { serviceKey ->
            api.getAbandonmentList(
                serviceKey = serviceKey,
                numOfRows = filter.numOfRows,
                pageNo = filter.pageNo,
                beginDate = filter.beginDate,
                endDate = filter.endDate,
                upKind = filter.upKind?.code,
                state = filter.state?.apiValue,
            )
        }

        val animals = body.items?.item.orEmpty().mapNotNull { it.toDomain() }
        return body.toPagedResult(animals)
    }
}
