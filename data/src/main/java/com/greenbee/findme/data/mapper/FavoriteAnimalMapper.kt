package com.greenbee.findme.data.mapper

import com.greenbee.findme.data.local.favorite.FavoriteAnimalEntity
import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.model.FavoriteAnimal

internal fun FavoriteAnimalEntity.toDomain(): FavoriteAnimal = FavoriteAnimal(
    desertionNo = desertionNo,
    savedAt = savedAt,
    animal = toAbandonedAnimal(),
)

internal fun FavoriteAnimalEntity.toAbandonedAnimal(): AbandonedAnimal = AbandonedAnimal(
    desertionNo = desertionNo,
    happenPlace = happenPlace,
    kindName = kindName,
    color = color,
    age = age,
    weight = weight,
    imageUrls = listOfNotNull(
        imageUrl1?.takeIf { it.isNotBlank() },
        imageUrl2?.takeIf { it.isNotBlank() },
    ),
    processState = processState,
    shelterName = shelterName,
    shelterTel = shelterTel,
    specialMark = specialMark,
    sex = sex,
    neuterYn = neuterYn,
)

internal fun AbandonedAnimal.toFavoriteEntity(savedAt: Long = System.currentTimeMillis()): FavoriteAnimalEntity =
    FavoriteAnimalEntity(
        desertionNo = desertionNo,
        kindName = kindName,
        processState = processState,
        shelterName = shelterName,
        shelterTel = shelterTel,
        imageUrl1 = imageUrls.getOrNull(0),
        imageUrl2 = imageUrls.getOrNull(1),
        happenPlace = happenPlace,
        color = color,
        age = age,
        weight = weight,
        specialMark = specialMark,
        sex = sex,
        neuterYn = neuterYn,
        savedAt = savedAt,
    )
