package com.greenbee.findme.presentation.my

import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.presentation.my.model.RecentAnimalUiModel

data class MyRecentUiState(
    val recentAnimals: List<RecentAnimalUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val showClearConfirmDialog: Boolean = false,
    val layoutMode: HomeLayoutMode = HomeLayoutMode.GRID,
)
