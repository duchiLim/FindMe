package com.greenbee.findme.presentation.shelter.animals

import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.presentation.home.model.AbandonedAnimalUiModel

data class ShelterAnimalsUiState(
    val shelterName: String = "",
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val animals: List<AbandonedAnimalUiModel> = emptyList(),
    val totalCount: Int = 0,
    val canLoadMore: Boolean = false,
    val isEmpty: Boolean = false,
    val errorMessage: String? = null,
    val layoutMode: HomeLayoutMode = HomeLayoutMode.GRID,
)
