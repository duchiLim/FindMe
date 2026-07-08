package com.greenbee.findme.presentation.favorite

import com.greenbee.findme.presentation.home.model.AbandonedAnimalUiModel

data class FavoriteUiState(
    val favorites: List<AbandonedAnimalUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isEmpty: Boolean = false,
)
