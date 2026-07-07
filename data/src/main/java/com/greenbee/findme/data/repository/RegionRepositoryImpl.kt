package com.greenbee.findme.data.repository

import com.greenbee.findme.data.mapper.toDomain
import com.greenbee.findme.data.mapper.toPagedResult
import com.greenbee.findme.data.remote.AbandonmentPublicApi
import com.greenbee.findme.data.remote.AbandonmentPublicRemoteDataSource
import com.greenbee.findme.domain.model.PagedResult
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.domain.repository.RegionRepository
import javax.inject.Inject

class RegionRepositoryImpl @Inject constructor(
    private val remoteDataSource: AbandonmentPublicRemoteDataSource,
    private val api: AbandonmentPublicApi,
) : RegionRepository {

    override suspend fun getSidoList(
        pageNo: Int,
        numOfRows: Int,
    ): PagedResult<Region> {
        val body = remoteDataSource.fetch { serviceKey ->
            api.getSidoList(
                serviceKey = serviceKey,
                numOfRows = numOfRows,
                pageNo = pageNo,
            )
        }

        val regions = body.items?.item.orEmpty().mapNotNull { it.toDomain() }
        return body.toPagedResult(regions)
    }

    override suspend fun getSigunguList(
        sidoCode: String,
        pageNo: Int,
        numOfRows: Int,
    ): PagedResult<Region> {
        val body = remoteDataSource.fetch { serviceKey ->
            api.getSigunguList(
                serviceKey = serviceKey,
                sidoCode = sidoCode,
                numOfRows = numOfRows,
                pageNo = pageNo,
            )
        }

        val regions = body.items?.item.orEmpty().mapNotNull { it.toDomain() }
        return body.toPagedResult(regions)
    }
}
