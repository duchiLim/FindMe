package com.greenbee.findme.presentation.my

import com.greenbee.findme.presentation.my.model.RecentAnimalUiModel

data class MyUiState(
    val recentAnimals: List<RecentAnimalUiModel> = emptyList(),
    val isRecentViewingEnabled: Boolean = true,
    val isNotificationEnabled: Boolean = false,
    val isLoading: Boolean = true,
    val appVersionName: String = "",
    val appVersionCode: Int = 0,
)
