package com.greenbee.findme.domain.model

data class FavoriteAnimal(
    val desertionNo: String,
    val savedAt: Long,
    val animal: AbandonedAnimal,
)
