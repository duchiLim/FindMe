package com.greenbee.findme.data.mapper

import com.greenbee.findme.data.local.recent.RecentlyViewedEntity
import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.model.RecentlyViewedAnimal

internal fun RecentlyViewedEntity.toDomain(): RecentlyViewedAnimal = RecentlyViewedAnimal(
    desertionNo = desertionNo,
    viewedAt = viewedAt,
    animal = toAbandonedAnimal(),
)

internal fun RecentlyViewedEntity.toAbandonedAnimal(): AbandonedAnimal = AbandonedAnimal(
    desertionNo = desertionNo,
    happenPlace = "",
    kindName = kindName,
    color = "",
    age = "",
    weight = "",
    imageUrls = listOfNotNull(
        imageUrl1?.takeIf { it.isNotBlank() },
        imageUrl2?.takeIf { it.isNotBlank() },
    ),
    processState = processState,
    shelterName = shelterName,
    shelterTel = "",
    specialMark = "",
    sex = "",
    neuterYn = "",
)

internal fun AbandonedAnimal.toRecentlyViewedEntity(
    viewedAt: Long = System.currentTimeMillis(),
): RecentlyViewedEntity = RecentlyViewedEntity(
    desertionNo = desertionNo,
    kindName = kindName,
    processState = processState,
    shelterName = shelterName,
    imageUrl1 = imageUrls.getOrNull(0),
    imageUrl2 = imageUrls.getOrNull(1),
    viewedAt = viewedAt,
)
