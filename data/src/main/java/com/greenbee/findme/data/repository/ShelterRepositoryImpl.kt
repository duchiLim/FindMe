package com.greenbee.findme.data.repository

import com.greenbee.findme.data.mapper.toDomain
import com.greenbee.findme.data.mapper.toPagedResult
import com.greenbee.findme.data.remote.AbandonmentPublicApi
import com.greenbee.findme.data.remote.AbandonmentPublicRemoteDataSource
import com.greenbee.findme.domain.model.PagedResult
import com.greenbee.findme.domain.model.Shelter
import com.greenbee.findme.domain.repository.ShelterRepository
import javax.inject.Inject

class ShelterRepositoryImpl @Inject constructor(
    private val remoteDataSource: AbandonmentPublicRemoteDataSource,
    private val api: AbandonmentPublicApi,
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
        return body.toPagedResult(shelters)
    }
}
