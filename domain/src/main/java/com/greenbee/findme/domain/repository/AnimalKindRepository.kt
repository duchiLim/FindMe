package com.greenbee.findme.domain.repository

import com.greenbee.findme.domain.model.AnimalKind
import com.greenbee.findme.domain.model.PagedResult
import com.greenbee.findme.domain.model.UpKind

interface AnimalKindRepository {
    suspend fun getKinds(
        upKind: UpKind,
        pageNo: Int = 1,
        numOfRows: Int = 200,
    ): PagedResult<AnimalKind>
}
