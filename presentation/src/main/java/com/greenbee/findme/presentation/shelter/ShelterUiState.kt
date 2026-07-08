package com.greenbee.findme.presentation.shelter

import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.presentation.shelter.model.ShelterUiModel

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
) {
    val regionSummary: String
        get() = when {
            selectedSido != null && selectedSigungu != null ->
                "${selectedSido.name} · ${selectedSigungu.name}"
            selectedSido != null -> selectedSido.name
            else -> ""
        }
}
