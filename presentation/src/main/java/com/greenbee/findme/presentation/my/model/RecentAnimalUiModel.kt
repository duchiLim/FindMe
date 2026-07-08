package com.greenbee.findme.presentation.my.model

import com.greenbee.findme.domain.model.RecentlyViewedAnimal
import com.greenbee.findme.presentation.home.model.ProcessStateType
import com.greenbee.findme.presentation.home.model.toProcessStateType
import com.greenbee.findme.presentation.home.model.toUiModel
import com.greenbee.findme.presentation.home.model.AbandonedAnimalUiModel
import com.greenbee.findme.presentation.my.util.formatRelativeTime

data class RecentAnimalUiModel(
    val desertionNo: String,
    val kindName: String,
    val processState: String,
    val shelterName: String,
    val imageUrls: List<String>,
    val statusType: ProcessStateType,
    val viewedAtLabel: String,
)

fun RecentlyViewedAnimal.toRecentUiModel(): RecentAnimalUiModel {
    val animalUi = animal.toUiModel()
    return RecentAnimalUiModel(
        desertionNo = animalUi.desertionNo,
        kindName = animalUi.kindName,
        processState = animalUi.processState,
        shelterName = animalUi.shelterName,
        imageUrls = animalUi.imageUrls,
        statusType = animal.processState.toProcessStateType(),
        viewedAtLabel = formatRelativeTime(viewedAt),
    )
}

fun RecentAnimalUiModel.toAnimalUiModel(): AbandonedAnimalUiModel =
    AbandonedAnimalUiModel(
        desertionNo = desertionNo,
        kindName = kindName,
        processState = processState,
        shelterName = shelterName,
        imageUrls = imageUrls,
        statusType = statusType,
    )
