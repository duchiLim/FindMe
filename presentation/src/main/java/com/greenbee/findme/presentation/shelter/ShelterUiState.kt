package com.greenbee.findme.presentation.shelter

import com.greenbee.findme.domain.model.GeoLocation
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.presentation.shelter.model.ShelterUiModel

enum class ShelterListMode {
    ALL,
    FAVORITES,
}

enum class ShelterSortMode {
    DEFAULT,
    DISTANCE,
}

data class ShelterUiState(
    val selectedSido: Region? = null,
    val selectedSigungu: Region? = null,
    val draftSido: Region? = null,
    val draftSigungu: Region? = null,
    val sidoList: List<Region> = emptyList(),
    val sigunguList: List<Region> = emptyList(),
    val shelters: List<ShelterUiModel> = emptyList(),
    val isSidoListLoading: Boolean = false,
    val isSigunguListLoading: Boolean = false,
    val isShelterListLoading: Boolean = false,
    val isRegionFilterSheetVisible: Boolean = false,
    val hasAppliedRegion: Boolean = false,
    val totalCount: Int = 0,
    val errorMessage: String? = null,
    val isEmpty: Boolean = false,
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,
    val listMode: ShelterListMode = ShelterListMode.ALL,
    val sortMode: ShelterSortMode = ShelterSortMode.DEFAULT,
    val userLocation: GeoLocation? = null,
    val isLocationLoading: Boolean = false,
    val locationMessage: String? = null,
    val favoriteCareRegNos: Set<String> = emptySet(),
    val favoriteShelters: List<ShelterUiModel> = emptyList(),
) {
    val regionSummary: String
        get() = when {
            selectedSido != null && selectedSigungu != null ->
                "${selectedSido.name} · ${selectedSigungu.name}"
            selectedSido != null -> selectedSido.name
            else -> ""
        }

    val displayShelters: List<ShelterUiModel>
        get() {
            val base = when (listMode) {
                ShelterListMode.ALL -> shelters
                ShelterListMode.FAVORITES -> favoriteShelters
            }
            val query = searchQuery.trim()
            val searched = if (query.isBlank()) {
                base
            } else {
                base.filter { shelter ->
                    shelter.name.contains(query, ignoreCase = true) ||
                        shelter.address.contains(query, ignoreCase = true) ||
                        shelter.orgName.contains(query, ignoreCase = true)
                }
            }
            return if (sortMode == ShelterSortMode.DISTANCE) {
                searched.sortedBy { it.distanceMeters ?: Int.MAX_VALUE }
            } else {
                searched
            }
        }

    val isSearchEmpty: Boolean
        get() = searchQuery.isNotBlank() && displayShelters.isEmpty() &&
            (if (listMode == ShelterListMode.ALL) shelters.isNotEmpty() else favoriteShelters.isNotEmpty())

    val isFavoritesEmpty: Boolean
        get() = listMode == ShelterListMode.FAVORITES && favoriteShelters.isEmpty()
}
