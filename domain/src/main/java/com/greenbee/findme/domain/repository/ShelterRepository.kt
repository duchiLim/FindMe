package com.greenbee.findme.domain.repository

import com.greenbee.findme.domain.model.PagedResult
import com.greenbee.findme.domain.model.Shelter

interface ShelterRepository {
    suspend fun getShelters(
        sidoCode: String,
        sigunguCode: String,
        pageNo: Int = 1,
        numOfRows: Int = 100,
    ): PagedResult<Shelter>
}
