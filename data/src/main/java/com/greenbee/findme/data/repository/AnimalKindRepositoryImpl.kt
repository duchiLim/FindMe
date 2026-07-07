package com.greenbee.findme.data.repository

import com.greenbee.findme.data.mapper.toDomain
import com.greenbee.findme.data.mapper.toPagedResult
import com.greenbee.findme.data.remote.AbandonmentPublicApi
import com.greenbee.findme.data.remote.AbandonmentPublicRemoteDataSource
import com.greenbee.findme.domain.model.AnimalKind
import com.greenbee.findme.domain.model.PagedResult
import com.greenbee.findme.domain.model.UpKind
import com.greenbee.findme.domain.repository.AnimalKindRepository
import javax.inject.Inject

class AnimalKindRepositoryImpl @Inject constructor(
    private val remoteDataSource: AbandonmentPublicRemoteDataSource,
    private val api: AbandonmentPublicApi,
) : AnimalKindRepository {

    override suspend fun getKinds(
        upKind: UpKind,
        pageNo: Int,
        numOfRows: Int,
    ): PagedResult<AnimalKind> {
        val body = remoteDataSource.fetch { serviceKey ->
            api.getKindList(
                serviceKey = serviceKey,
                upKindCode = upKind.code,
                numOfRows = numOfRows,
                pageNo = pageNo,
            )
        }

        val kinds = body.items?.item.orEmpty().mapNotNull { it.toDomain() }
        return body.toPagedResult(kinds)
    }
}
