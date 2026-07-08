package com.greenbee.findme.presentation.my

import com.greenbee.findme.presentation.home.model.AbandonedAnimalUiModel

data class MyUiState(
    val recentAnimals: List<AbandonedAnimalUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val appVersionName: String = "",
    val appVersionCode: Int = 0,
)
