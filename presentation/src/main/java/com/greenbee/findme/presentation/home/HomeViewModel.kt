package com.greenbee.findme.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.model.AbandonedAnimalFilter
import com.greenbee.findme.domain.model.AbandonmentState
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.domain.model.UpKind
import com.greenbee.findme.domain.model.FavoriteAnimal
import com.greenbee.findme.domain.usecase.FavoriteToggleResult
import com.greenbee.findme.domain.usecase.GetAbandonedAnimalsUseCase
import com.greenbee.findme.domain.usecase.GetHomeListCacheUseCase
import com.greenbee.findme.domain.usecase.GetSidoListUseCase
import com.greenbee.findme.domain.usecase.GetSigunguListUseCase
import com.greenbee.findme.domain.usecase.GetUserSettingsUseCase
import com.greenbee.findme.domain.usecase.ObserveFavoritesUseCase
import com.greenbee.findme.domain.usecase.RestoreFavoriteUseCase
import com.greenbee.findme.domain.usecase.SaveHomeListCacheUseCase
import com.greenbee.findme.domain.usecase.ToggleFavoriteUseCase
import com.greenbee.findme.presentation.home.model.HomeDateRange
import com.greenbee.findme.presentation.home.model.HomeSortOrder
import com.greenbee.findme.presentation.home.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAbandonedAnimalsUseCase: GetAbandonedAnimalsUseCase,
    private val getSidoListUseCase: GetSidoListUseCase,
    private val getSigunguListUseCase: GetSigunguListUseCase,
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    getUserSettingsUseCase: GetUserSettingsUseCase,
    private val getHomeListCacheUseCase: GetHomeListCacheUseCase,
    private val saveHomeListCacheUseCase: SaveHomeListCacheUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val restoreFavoriteUseCase: RestoreFavoriteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var isRequestInFlight = false
    private var rawFavorites: List<FavoriteAnimal> = emptyList()
    private var pendingUndoFavorite: FavoriteAnimal? = null
    private var latestDomainAnimals: List<com.greenbee.findme.domain.model.AbandonedAnimal> = emptyList()

    init {
        val settings = getUserSettingsUseCase()
        _uiState.update { it.copy(isRecentViewingEnabled = settings.isRecentViewingEnabled) }

        loadSidoList()
        viewModelScope.launch {
            getHomeListCacheUseCase()?.let { cache ->
                _uiState.update {
                    it.copy(
                        animals = cache.animals.map { animal -> animal.toUiModel() },
                        totalCount = cache.totalCount,
                        filterSummary = cache.filterSummary,
                        isLoading = false,
                    )
                }
                latestDomainAnimals = cache.animals
            }
        }
        loadAnimals(reset = true)
        viewModelScope.launch {
            observeFavoritesUseCase().collect { favorites ->
                rawFavorites = favorites
                _uiState.update {
                    it.copy(favoriteDesertionNos = favorites.map { favorite -> favorite.desertionNo }.toSet())
                }
            }
        }
    }

    fun toggleFavorite(desertionNo: String) {
        viewModelScope.launch {
            val favoriteBeforeRemove = rawFavorites.find { it.desertionNo == desertionNo }
            runCatching { toggleFavoriteUseCase(desertionNo) }
                .onSuccess { result ->
                    pendingUndoFavorite = if (result == FavoriteToggleResult.REMOVED) {
                        favoriteBeforeRemove
                    } else {
                        null
                    }
                    _uiState.update {
                        it.copy(
                            favoriteSnackbar = when (result) {
                                FavoriteToggleResult.ADDED -> HomeFavoriteSnackbar.ADDED
                                FavoriteToggleResult.REMOVED -> HomeFavoriteSnackbar.REMOVED
                            },
                        )
                    }
                }
        }
    }

    fun undoRemoveFavorite() {
        val favorite = pendingUndoFavorite ?: return
        viewModelScope.launch {
            restoreFavoriteUseCase(favorite)
            onFavoriteSnackbarShown()
        }
    }

    fun onFavoriteSnackbarShown() {
        pendingUndoFavorite = null
        _uiState.update { it.copy(favoriteSnackbar = null) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "") }
    }

    fun setSortOrder(sortOrder: HomeSortOrder) {
        _uiState.update { it.copy(sortOrder = sortOrder) }
    }

    fun onStateSelected(state: AbandonmentState) {
        val nextState = if (_uiState.value.selectedState == state) null else state
        _uiState.update { it.copy(selectedState = nextState) }
        loadAnimals(reset = true)
    }

    fun onAdvancedFilterClick() {
        val state = _uiState.value
        _uiState.update {
            it.copy(
                isAdvancedFilterSheetVisible = true,
                draftSido = state.selectedSido,
                draftSigungu = state.selectedSigungu,
                draftUpKind = state.selectedUpKind,
                draftState = state.selectedState,
                draftDateRange = state.selectedDateRange,
            )
        }
        state.selectedSido?.code?.let(::loadSigunguList)
    }

    fun onAdvancedFilterDismiss() {
        _uiState.update {
            it.copy(
                isAdvancedFilterSheetVisible = false,
                draftSido = it.selectedSido,
                draftSigungu = it.selectedSigungu,
                draftUpKind = it.selectedUpKind,
                draftState = it.selectedState,
                draftDateRange = it.selectedDateRange,
            )
        }
    }

    fun onDraftStateSelected(state: AbandonmentState?) {
        _uiState.update { it.copy(draftState = state) }
    }

    fun onDraftDateRangeSelected(dateRange: HomeDateRange) {
        _uiState.update { it.copy(draftDateRange = dateRange) }
    }

    fun onResetAdvancedFilterDraft() {
        _uiState.update {
            it.copy(
                draftSido = null,
                draftSigungu = null,
                draftUpKind = null,
                draftState = null,
                draftDateRange = HomeDateRange.DAYS_30,
                sigunguList = emptyList(),
            )
        }
    }

    fun onApplyAdvancedFilter() {
        _uiState.update {
            it.copy(
                selectedSido = it.draftSido,
                selectedSigungu = it.draftSigungu,
                selectedUpKind = it.draftUpKind,
                selectedState = it.draftState,
                selectedDateRange = it.draftDateRange,
                isAdvancedFilterSheetVisible = false,
            )
        }
        loadAnimals(reset = true)
    }

    fun onRegionFilterClick() {
        val state = _uiState.value
        _uiState.update {
            it.copy(
                isRegionFilterSheetVisible = true,
                draftSido = state.selectedSido,
                draftSigungu = state.selectedSigungu,
            )
        }
        state.selectedSido?.code?.let(::loadSigunguList)
    }

    fun onRegionFilterDismiss() {
        _uiState.update {
            it.copy(
                isRegionFilterSheetVisible = false,
                draftSido = it.selectedSido,
                draftSigungu = it.selectedSigungu,
            )
        }
    }

    fun onUpKindFilterClick() {
        _uiState.update {
            it.copy(
                isUpKindFilterSheetVisible = true,
                draftUpKind = it.selectedUpKind,
            )
        }
    }

    fun onUpKindFilterDismiss() {
        _uiState.update {
            it.copy(
                isUpKindFilterSheetVisible = false,
                draftUpKind = it.selectedUpKind,
            )
        }
    }

    fun onDraftSidoSelected(sido: Region?) {
        _uiState.update {
            it.copy(
                draftSido = sido,
                draftSigungu = null,
                sigunguList = emptyList(),
            )
        }
        if (sido != null) {
            loadSigunguList(sido.code)
        }
    }

    fun onDraftSigunguSelected(sigungu: Region?) {
        _uiState.update { it.copy(draftSigungu = sigungu) }
    }

    fun onDraftUpKindSelected(upKind: UpKind?) {
        _uiState.update { it.copy(draftUpKind = upKind) }
    }

    fun onResetRegionDraft() {
        _uiState.update {
            it.copy(
                draftSido = null,
                draftSigungu = null,
                sigunguList = emptyList(),
            )
        }
    }

    fun onResetUpKindDraft() {
        _uiState.update { it.copy(draftUpKind = null) }
    }

    fun onApplyRegionFilter() {
        _uiState.update {
            it.copy(
                selectedSido = it.draftSido,
                selectedSigungu = it.draftSigungu,
                isRegionFilterSheetVisible = false,
            )
        }
        loadAnimals(reset = true)
    }

    fun onApplyUpKindFilter() {
        _uiState.update {
            it.copy(
                selectedUpKind = it.draftUpKind,
                isUpKindFilterSheetVisible = false,
            )
        }
        loadAnimals(reset = true)
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.canLoadMore || isRequestInFlight) return
        loadAnimals(reset = false)
    }

    fun clearFilters() {
        if (!hasActiveFilters(_uiState.value)) {
            retry()
            return
        }
        _uiState.update {
            it.copy(
                selectedUpKind = null,
                selectedState = null,
                selectedSido = null,
                selectedSigungu = null,
                selectedDateRange = HomeDateRange.DAYS_30,
                draftSido = null,
                draftSigungu = null,
                draftUpKind = null,
                draftState = null,
                draftDateRange = HomeDateRange.DAYS_30,
                sigunguList = emptyList(),
                searchQuery = "",
            )
        }
        loadAnimals(reset = true)
    }

    fun retry() {
        loadAnimals(reset = _uiState.value.animals.isEmpty())
    }

    fun refresh() {
        if (isRequestInFlight || _uiState.value.isRefreshing) return
        loadAnimals(reset = true, fromPullRefresh = true)
    }

    private fun loadSidoList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSidoListLoading = true) }
            runCatching { getSidoListUseCase() }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            sidoList = result.items,
                            isSidoListLoading = false,
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isSidoListLoading = false) }
                }
        }
    }

    private fun loadSigunguList(sidoCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSigunguListLoading = true, sigunguList = emptyList()) }
            runCatching { getSigunguListUseCase(sidoCode) }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            sigunguList = result.items,
                            isSigunguListLoading = false,
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isSigunguListLoading = false) }
                }
        }
    }

    private fun loadAnimals(reset: Boolean, fromPullRefresh: Boolean = false) {
        if (isRequestInFlight) return
        isRequestInFlight = true

        val page = if (reset) 1 else currentPage + 1
        val currentState = _uiState.value

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = reset && !fromPullRefresh && it.animals.isEmpty(),
                    isFilterLoading = reset && !fromPullRefresh && it.animals.isNotEmpty(),
                    isRefreshing = fromPullRefresh,
                    isLoadingMore = !reset,
                    errorMessage = null,
                    isEmpty = false,
                )
            }

            runCatching {
                getAbandonedAnimalsUseCase(buildFilter(page, currentState))
            }.onSuccess { result ->
                currentPage = page
                val newAnimals = result.items.map { it.toUiModel() }
                val mergedAnimals = (if (reset) newAnimals else currentState.animals + newAnimals)
                    .distinctBy { it.desertionNo }
                val canLoadMore = mergedAnimals.size < result.totalCount
                latestDomainAnimals = if (reset) result.items else latestDomainAnimals + result.items
                val filterSummary = buildFilterSummary(
                    upKind = currentState.selectedUpKind,
                    state = currentState.selectedState,
                    sido = currentState.selectedSido,
                    sigungu = currentState.selectedSigungu,
                    dateRange = currentState.selectedDateRange,
                )

                if (reset) {
                    saveHomeListCacheUseCase(
                        animals = result.items,
                        totalCount = result.totalCount,
                        filterSummary = filterSummary,
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isFilterLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        animals = mergedAnimals,
                        totalCount = result.totalCount,
                        filterSummary = filterSummary,
                        canLoadMore = canLoadMore,
                        isEmpty = mergedAnimals.isEmpty(),
                        errorMessage = null,
                        showOfflineBanner = false,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isFilterLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        errorMessage = if (it.animals.isEmpty()) {
                            error.message ?: "유기동물 정보를 불러오지 못했습니다."
                        } else {
                            null
                        },
                        showOfflineBanner = fromPullRefresh && it.animals.isNotEmpty(),
                    )
                }
            }

            isRequestInFlight = false
        }
    }

    private fun buildFilter(page: Int, state: HomeUiState): AbandonedAnimalFilter {
        val end = LocalDate.now()
        val begin = end.minusDays(state.selectedDateRange.days.toLong())
        return AbandonedAnimalFilter(
            pageNo = page,
            numOfRows = PAGE_SIZE,
            beginDate = begin.format(DATE_FORMATTER),
            endDate = end.format(DATE_FORMATTER),
            upKind = state.selectedUpKind,
            state = state.selectedState,
            sidoCode = state.selectedSido?.code,
            sigunguCode = state.selectedSigungu?.code,
        )
    }

    private fun buildFilterSummary(
        upKind: UpKind?,
        state: AbandonmentState?,
        sido: Region?,
        sigungu: Region?,
        dateRange: HomeDateRange,
    ): String = buildList {
        when {
            sigungu != null -> add(sigungu.name)
            sido != null -> add(sido.name)
        }
        when (upKind) {
            UpKind.DOG -> add("개")
            UpKind.CAT -> add("고양이")
            UpKind.ETC -> add("기타")
            null -> Unit
        }
        when (state) {
            AbandonmentState.NOTICE -> add("공고중")
            AbandonmentState.PROTECT -> add("보호중")
            null -> Unit
        }
        add(
            when (dateRange) {
                HomeDateRange.DAYS_7 -> "최근 7일"
                HomeDateRange.DAYS_30 -> "최근 30일"
                HomeDateRange.DAYS_90 -> "최근 90일"
            },
        )
    }.joinToString(separator = " · ")

    private fun hasActiveFilters(state: HomeUiState): Boolean =
        state.selectedUpKind != null ||
            state.selectedState != null ||
            state.selectedSido != null ||
            state.selectedSigungu != null ||
            state.selectedDateRange != HomeDateRange.DAYS_30

    private companion object {
        const val PAGE_SIZE = 20
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE
    }
}
