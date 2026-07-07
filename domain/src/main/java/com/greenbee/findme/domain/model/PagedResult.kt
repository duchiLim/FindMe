package com.greenbee.findme.domain.model

data class PagedResult<T>(
    val items: List<T>,
    val pageNo: Int,
    val numOfRows: Int,
    val totalCount: Int,
)
