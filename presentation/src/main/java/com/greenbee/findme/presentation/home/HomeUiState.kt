package com.greenbee.findme.presentation.home

import com.greenbee.findme.domain.model.AbandonmentState
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.domain.model.UpKind
import com.greenbee.findme.presentation.home.model.AbandonedAnimalUiModel

data class HomeUiState(
    val isLoading: Boolean = true,
    val isFilterLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val animals: List<AbandonedAnimalUiModel> = emptyList(),
    val totalCount: Int = 0,
    val selectedUpKind: UpKind? = null,
    val selectedState: AbandonmentState? = null,
    val selectedSido: Region? = null,
    val selectedSigungu: Region? = null,
    val draftSido: Region? = null,
    val draftSigungu: Region? = null,
    val draftUpKind: UpKind? = null,
    val sidoList: List<Region> = emptyList(),
    val sigunguList: List<Region> = emptyList(),
    val isSidoListLoading: Boolean = false,
    val isSigunguListLoading: Boolean = false,
    val isRegionFilterSheetVisible: Boolean = false,
    val isUpKindFilterSheetVisible: Boolean = false,
    val filterSummary: String = "",
    val canLoadMore: Boolean = false,
    val isEmpty: Boolean = false,
    val errorMessage: String? = null,
) {
    val activeRegionFilterCount: Int
        get() = (if (selectedSido != null) 1 else 0) + (if (selectedSigungu != null) 1 else 0)
}
