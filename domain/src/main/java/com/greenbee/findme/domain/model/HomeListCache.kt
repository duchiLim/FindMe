package com.greenbee.findme.domain.model

data class HomeListCache(
    val animals: List<AbandonedAnimal>,
    val totalCount: Int,
    val filterSummary: String,
)
