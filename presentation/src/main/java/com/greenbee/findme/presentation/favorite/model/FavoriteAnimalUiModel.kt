package com.greenbee.findme.presentation.favorite.model

import com.greenbee.findme.domain.model.FavoriteAnimal
import com.greenbee.findme.presentation.home.model.AbandonedAnimalUiModel
import com.greenbee.findme.presentation.home.model.isEndedNotice
import com.greenbee.findme.presentation.home.model.toUiModel

data class FavoriteAnimalUiModel(
    val animal: AbandonedAnimalUiModel,
    val savedAt: Long,
) {
    val desertionNo: String get() = animal.desertionNo
    val isEnded: Boolean get() = animal.isEndedNotice()
}

fun FavoriteAnimal.toFavoriteUiModel(): FavoriteAnimalUiModel =
    FavoriteAnimalUiModel(
        animal = animal.toUiModel(),
        savedAt = savedAt,
    )

fun List<FavoriteAnimalUiModel>.sortedBy(
    sortOrder: com.greenbee.findme.presentation.favorite.FavoriteSortOrder,
): List<FavoriteAnimalUiModel> =
    when (sortOrder) {
        com.greenbee.findme.presentation.favorite.FavoriteSortOrder.SAVED_AT_DESC ->
            sortedByDescending { it.savedAt }
        com.greenbee.findme.presentation.favorite.FavoriteSortOrder.STATUS_NOTICE_FIRST ->
            sortedWith(
                compareBy<FavoriteAnimalUiModel> { it.isEnded }
                    .thenByDescending { it.savedAt },
            )
    }
