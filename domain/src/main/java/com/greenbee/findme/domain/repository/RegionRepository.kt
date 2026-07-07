package com.greenbee.findme.domain.repository

import com.greenbee.findme.domain.model.PagedResult
import com.greenbee.findme.domain.model.Region

interface RegionRepository {
    suspend fun getSidoList(pageNo: Int = 1, numOfRows: Int = 100): PagedResult<Region>

    suspend fun getSigunguList(
        sidoCode: String,
        pageNo: Int = 1,
        numOfRows: Int = 100,
    ): PagedResult<Region>
}
