package com.greenbee.findme.presentation.shelter

import com.greenbee.findme.domain.model.GeoLocation
import com.greenbee.findme.presentation.shelter.model.ShelterUiModel

enum class ShelterFavoriteMessage {
    ADDED,
    REMOVED,
}

data class ShelterDetailUiState(
    val isLoading: Boolean = true,
    val shelter: ShelterUiModel? = null,
    val mapLocation: GeoLocation? = null,
    val isMapLoading: Boolean = false,
    val errorMessage: String? = null,
    val isNotFound: Boolean = false,
    val isFavorite: Boolean = false,
    val favoriteMessage: ShelterFavoriteMessage? = null,
)
