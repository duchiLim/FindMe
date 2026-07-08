package com.greenbee.findme.presentation.home

import com.greenbee.findme.domain.model.AbandonmentState
import com.greenbee.findme.domain.model.HomeDateRange
import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.domain.model.UpKind
import com.greenbee.findme.presentation.home.model.AbandonedAnimalUiModel
import com.greenbee.findme.presentation.home.model.HomeSortOrder
import com.greenbee.findme.presentation.home.model.filterBySearch
import com.greenbee.findme.presentation.home.model.isAdoptionImminent
import com.greenbee.findme.presentation.home.model.isRegisteredToday
import com.greenbee.findme.presentation.home.model.HOME_CURATION_LIMIT
import com.greenbee.findme.presentation.home.model.sortedBy

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
    val selectedDateRange: HomeDateRange = HomeDateRange.DAYS_30,
    val draftSido: Region? = null,
    val draftSigungu: Region? = null,
    val draftUpKind: UpKind? = null,
    val draftState: AbandonmentState? = null,
    val draftDateRange: HomeDateRange = HomeDateRange.DAYS_30,
    val sidoList: List<Region> = emptyList(),
    val sigunguList: List<Region> = emptyList(),
    val isSidoListLoading: Boolean = false,
    val isSigunguListLoading: Boolean = false,
    val isRegionFilterSheetVisible: Boolean = false,
    val isUpKindFilterSheetVisible: Boolean = false,
    val isAdvancedFilterSheetVisible: Boolean = false,
    val filterSummary: String = "",
    val canLoadMore: Boolean = false,
    val isEmpty: Boolean = false,
    val errorMessage: String? = null,
    val showOfflineBanner: Boolean = false,
    val favoriteDesertionNos: Set<String> = emptySet(),
    val favoriteSnackbar: HomeFavoriteSnackbar? = null,
    val isRecentViewingEnabled: Boolean = true,
    val searchQuery: String = "",
    val sortOrder: HomeSortOrder = HomeSortOrder.LATEST,
    val layoutMode: HomeLayoutMode = HomeLayoutMode.GRID,
) {
    val displayAnimals: List<AbandonedAnimalUiModel>
        get() = animals.filterBySearch(searchQuery).sortedBy(sortOrder)

    val isSearchEmpty: Boolean
        get() = searchQuery.isNotBlank() && displayAnimals.isEmpty() && !isLoading && !isFilterLoading

    val todayRegisteredAnimals: List<AbandonedAnimalUiModel>
        get() = if (searchQuery.isNotBlank()) {
            emptyList()
        } else {
            animals.filter { it.isRegisteredToday() }.take(HOME_CURATION_LIMIT)
        }

    val adoptionImminentAnimals: List<AbandonedAnimalUiModel>
        get() = if (searchQuery.isNotBlank()) {
            emptyList()
        } else {
            animals.filter { it.isAdoptionImminent() }.take(HOME_CURATION_LIMIT)
        }

    val activeRegionFilterCount: Int
        get() = (if (selectedSido != null) 1 else 0) + (if (selectedSigungu != null) 1 else 0)

    val activeAdvancedFilterCount: Int
        get() = activeRegionFilterCount +
            (if (selectedUpKind != null) 1 else 0) +
            (if (selectedState != null) 1 else 0) +
            (if (selectedDateRange != HomeDateRange.DAYS_30) 1 else 0)
}
