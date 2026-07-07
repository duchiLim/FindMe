package com.greenbee.findme.presentation.detail

import com.greenbee.findme.presentation.detail.model.AnimalDetailUiModel

data class AnimalDetailUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val animal: AnimalDetailUiModel? = null,
    val errorMessage: String? = null,
    val isNotFound: Boolean = false,
)
