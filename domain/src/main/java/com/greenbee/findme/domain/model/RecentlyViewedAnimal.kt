package com.greenbee.findme.domain.model

data class RecentlyViewedAnimal(
    val desertionNo: String,
    val viewedAt: Long,
    val animal: AbandonedAnimal,
)
