package com.greenbee.findme.domain.model

data class AbandonedAnimalFilter(
    val pageNo: Int = 1,
    val numOfRows: Int = 20,
    val beginDate: String? = null,
    val endDate: String? = null,
    val upKind: UpKind? = null,
    val state: AbandonmentState? = null,
)
