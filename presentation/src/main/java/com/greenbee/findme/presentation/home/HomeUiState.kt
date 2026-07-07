package com.greenbee.findme.presentation.home

import com.greenbee.findme.domain.model.AbandonmentState
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.domain.model.UpKind
import com.greenbee.findme.presentation.home.model.AbandonedAnimalUiModel

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val animals: List<AbandonedAnimalUiModel> = emptyList(),
    val totalCount: Int = 0,
    val selectedUpKind: UpKind? = null,
    val selectedState: AbandonmentState? = null,
    val selectedSido: Region? = null,
    val sidoList: List<Region> = emptyList(),
    val isSidoListLoading: Boolean = false,
    val isSidoFilterSheetVisible: Boolean = false,
    val filterSummary: String = "",
    val canLoadMore: Boolean = false,
    val isEmpty: Boolean = false,
    val errorMessage: String? = null,
)
