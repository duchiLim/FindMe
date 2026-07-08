package com.greenbee.findme.presentation.home.today

import com.greenbee.findme.presentation.home.HomeFavoriteSnackbar
import com.greenbee.findme.presentation.home.model.AbandonedAnimalUiModel

data class HomeTodayRegisteredUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val animals: List<AbandonedAnimalUiModel> = emptyList(),
    val totalCount: Int = 0,
    val canLoadMore: Boolean = false,
    val isEmpty: Boolean = false,
    val errorMessage: String? = null,
    val favoriteDesertionNos: Set<String> = emptySet(),
    val favoriteSnackbar: HomeFavoriteSnackbar? = null,
)
