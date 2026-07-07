package com.greenbee.findme.data.repository

import com.greenbee.findme.data.local.AbandonedAnimalMemoryCache
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
    private val memoryCache: AbandonedAnimalMemoryCache,
) : AbandonedAnimalRepository {

    override suspend fun getAbandonedAnimals(
        filter: AbandonedAnimalFilter,
    ): PagedResult<AbandonedAnimal> {
        val resolvedFilter = filter.withDefaultDateRange()
        val body = remoteDataSource.fetch { serviceKey ->
            api.getAbandonmentList(
                serviceKey = serviceKey,
                numOfRows = resolvedFilter.numOfRows,
                pageNo = resolvedFilter.pageNo,
                beginDate = resolvedFilter.beginDate,
                endDate = resolvedFilter.endDate,
                upKind = resolvedFilter.upKind?.code,
                sidoCode = resolvedFilter.sidoCode,
                sigunguCode = resolvedFilter.sigunguCode,
                state = resolvedFilter.state?.apiValue,
                desertionNo = resolvedFilter.desertionNo,
            )
        }

        val animals = body.items?.item.orEmpty().mapNotNull { it.toDomain() }
        memoryCache.putAll(animals)
        return body.toPagedResult(animals)
    }

    override fun getCachedAbandonedAnimal(desertionNo: String): AbandonedAnimal? =
        memoryCache.get(desertionNo)

    override suspend fun getAbandonedAnimal(desertionNo: String): AbandonedAnimal? {
        val filter = AbandonedAnimalFilter(
            desertionNo = desertionNo,
            pageNo = 1,
            numOfRows = 1,
        )
        val animal = getAbandonedAnimals(filter).items.firstOrNull()
        return animal ?: memoryCache.get(desertionNo)
    }
}
