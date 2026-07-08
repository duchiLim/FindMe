package com.greenbee.findme.presentation.home.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.model.AbandonedAnimalFilter
import com.greenbee.findme.domain.model.FavoriteAnimal
import com.greenbee.findme.domain.model.PagedResult
import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.usecase.FavoriteToggleResult
import com.greenbee.findme.domain.usecase.GetAbandonedAnimalsUseCase
import com.greenbee.findme.domain.usecase.ObserveFavoritesUseCase
import com.greenbee.findme.domain.usecase.RestoreFavoriteUseCase
import com.greenbee.findme.domain.usecase.ToggleFavoriteUseCase
import com.greenbee.findme.presentation.home.HomeFavoriteSnackbar
import com.greenbee.findme.presentation.home.model.AbandonedAnimalUiModel
import com.greenbee.findme.presentation.home.model.isRegisteredToday
import com.greenbee.findme.presentation.home.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeTodayRegisteredViewModel @Inject constructor(
    private val getAbandonedAnimalsUseCase: GetAbandonedAnimalsUseCase,
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val restoreFavoriteUseCase: RestoreFavoriteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeTodayRegisteredUiState())
    val uiState: StateFlow<HomeTodayRegisteredUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var isRequestInFlight = false
    private var rawFavorites: List<FavoriteAnimal> = emptyList()
    private var pendingUndoFavorite: FavoriteAnimal? = null

    init {
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

    fun retry() {
        loadAnimals(reset = _uiState.value.animals.isEmpty())
    }

    fun refresh() {
        if (isRequestInFlight || _uiState.value.isRefreshing) return
        loadAnimals(reset = true, fromPullRefresh = true)
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.canLoadMore || isRequestInFlight) return
        loadAnimals(reset = false)
    }

    private fun loadAnimals(reset: Boolean, fromPullRefresh: Boolean = false) {
        if (isRequestInFlight) return
        isRequestInFlight = true

        val startPage = if (reset) 1 else currentPage + 1
        val existingAnimals = if (reset) emptyList() else _uiState.value.animals

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = reset && !fromPullRefresh && it.animals.isEmpty(),
                    isRefreshing = fromPullRefresh,
                    isLoadingMore = !reset,
                    errorMessage = null,
                    isEmpty = false,
                )
            }

            runCatching {
                fetchTodayAnimals(
                    startPage = startPage,
                    existingAnimals = existingAnimals,
                    scanEmptyPages = reset,
                )
            }.onSuccess { result ->
                currentPage = result.lastLoadedPage
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        animals = result.animals,
                        totalCount = result.animals.size,
                        canLoadMore = result.canLoadMore,
                        isEmpty = result.animals.isEmpty() && !result.canLoadMore,
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        errorMessage = error.message ?: "유기동물 정보를 불러오지 못했습니다.",
                    )
                }
            }

            isRequestInFlight = false
        }
    }

    private suspend fun fetchTodayAnimals(
        startPage: Int,
        existingAnimals: List<AbandonedAnimalUiModel>,
        scanEmptyPages: Boolean,
    ): TodayFetchResult {
        val today = LocalDate.now()
        var page = startPage
        var mergedAnimals = existingAnimals
        var pagesLoadedThisRequest = 0
        val maxPagesPerRequest = if (scanEmptyPages) {
            MAX_EMPTY_PAGE_SCAN
        } else {
            MAX_LOAD_MORE_PAGE_SCAN
        }

        while (true) {
            val result = fetchPage(page, today)
            val newTodayAnimals = result.items
                .map { it.toUiModel() }
                .filter { it.isRegisteredToday() }
            mergedAnimals = (mergedAnimals + newTodayAnimals).distinctBy { it.desertionNo }

            val canLoadMore = page * PAGE_SIZE < result.totalCount
            pagesLoadedThisRequest++

            val shouldContinue = canLoadMore &&
                newTodayAnimals.isEmpty() &&
                pagesLoadedThisRequest < maxPagesPerRequest

            if (!shouldContinue) {
                return TodayFetchResult(
                    animals = mergedAnimals,
                    lastLoadedPage = page,
                    canLoadMore = canLoadMore,
                )
            }

            page++
        }
    }

    private suspend fun fetchPage(
        page: Int,
        today: LocalDate,
    ): PagedResult<AbandonedAnimal> {
        val begin = today.minusDays(SCAN_DAYS.toLong()).format(DATE_FORMATTER)
        val end = today.format(DATE_FORMATTER)
        return getAbandonedAnimalsUseCase(
            AbandonedAnimalFilter(
                pageNo = page,
                numOfRows = PAGE_SIZE,
                beginDate = begin,
                endDate = end,
            ),
        )
    }

    private data class TodayFetchResult(
        val animals: List<AbandonedAnimalUiModel>,
        val lastLoadedPage: Int,
        val canLoadMore: Boolean,
    )

    private companion object {
        const val PAGE_SIZE = 20
        const val SCAN_DAYS = 30L
        const val MAX_EMPTY_PAGE_SCAN = 10
        const val MAX_LOAD_MORE_PAGE_SCAN = 5
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE
    }
}
