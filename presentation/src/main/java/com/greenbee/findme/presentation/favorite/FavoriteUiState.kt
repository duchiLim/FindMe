package com.greenbee.findme.presentation.favorite

import com.greenbee.findme.domain.model.FavoriteAnimal
import com.greenbee.findme.presentation.favorite.model.FavoriteAnimalUiModel

enum class FavoriteSortOrder {
    SAVED_AT_DESC,
    STATUS_NOTICE_FIRST,
}

data class FavoriteUiState(
    val favorites: List<FavoriteAnimalUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isEmpty: Boolean = false,
    val sortOrder: FavoriteSortOrder = FavoriteSortOrder.SAVED_AT_DESC,
    val refreshFailedCount: Int = 0,
    val pendingUndoFavorite: FavoriteAnimal? = null,
    val showRemovedSnackbar: Boolean = false,
) {
    val endedCount: Int
        get() = favorites.count { it.isEnded }
}
