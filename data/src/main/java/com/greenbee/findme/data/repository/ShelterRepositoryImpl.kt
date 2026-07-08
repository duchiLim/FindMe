package com.greenbee.findme.data.repository

import com.greenbee.findme.data.mapper.mergeWithDetail
import com.greenbee.findme.data.mapper.toDomain
import com.greenbee.findme.data.mapper.toPagedResult
import com.greenbee.findme.data.remote.AbandonmentPublicApi
import com.greenbee.findme.data.remote.AbandonmentPublicRemoteDataSource
import com.greenbee.findme.data.remote.AnimalShelterApi
import com.greenbee.findme.data.remote.AnimalShelterRemoteDataSource
import com.greenbee.findme.domain.model.PagedResult
import com.greenbee.findme.domain.model.Shelter
import com.greenbee.findme.domain.repository.ShelterRepository
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ShelterRepositoryImpl @Inject constructor(
    private val remoteDataSource: AbandonmentPublicRemoteDataSource,
    private val shelterInfoRemoteDataSource: AnimalShelterRemoteDataSource,
    private val api: AbandonmentPublicApi,
    private val shelterInfoApi: AnimalShelterApi,
) : ShelterRepository {

    override suspend fun getShelters(
        sidoCode: String,
        sigunguCode: String,
        pageNo: Int,
        numOfRows: Int,
    ): PagedResult<Shelter> {
        val body = remoteDataSource.fetch { serviceKey ->
            api.getShelterList(
                serviceKey = serviceKey,
                sidoCode = sidoCode,
                sigunguCode = sigunguCode,
                numOfRows = numOfRows,
                pageNo = pageNo,
            )
        }

        val shelters = body.items?.item.orEmpty().mapNotNull { it.toDomain() }
        val enrichedShelters = enrichShelters(shelters)
        return body.toPagedResult(enrichedShelters)
    }

    override suspend fun getShelter(careRegNo: String): Shelter? =
        fetchShelterInfo(careRegNo = careRegNo)

    override suspend fun findShelterByName(name: String): Shelter? {
        if (name.isBlank()) return null
        return fetchShelterInfo(careName = name)
    }

    private suspend fun enrichShelters(shelters: List<Shelter>): List<Shelter> = coroutineScope {
        shelters.map { shelter ->
            async {
                fetchShelterInfo(careRegNo = shelter.careRegNo)?.let { detail ->
                    shelter.mergeWithDetail(detail)
                } ?: shelter
            }
        }.awaitAll()
    }

    private suspend fun fetchShelterInfo(
        careRegNo: String? = null,
        careName: String? = null,
    ): Shelter? {
        val body = shelterInfoRemoteDataSource.fetch { serviceKey ->
            shelterInfoApi.getShelterInfo(
                serviceKey = serviceKey,
                careRegNo = careRegNo,
                careName = careName,
                numOfRows = 1,
                pageNo = 1,
            )
        }
        return body.items?.item.orEmpty().firstOrNull()?.toDomain()
    }
}
