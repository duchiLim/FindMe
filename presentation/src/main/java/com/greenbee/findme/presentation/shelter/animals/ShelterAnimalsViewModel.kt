package com.greenbee.findme.presentation.shelter.animals

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.model.AbandonedAnimalFilter
import com.greenbee.findme.domain.model.FavoriteAnimal
import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.domain.usecase.FavoriteToggleResult
import com.greenbee.findme.domain.usecase.GetAbandonedAnimalsUseCase
import com.greenbee.findme.domain.usecase.ObserveFavoritesUseCase
import com.greenbee.findme.domain.usecase.ObserveShelterLayoutModeUseCase
import com.greenbee.findme.domain.usecase.RestoreFavoriteUseCase
import com.greenbee.findme.domain.usecase.SetShelterLayoutModeUseCase
import com.greenbee.findme.domain.usecase.ToggleFavoriteUseCase
import com.greenbee.findme.presentation.home.HomeFavoriteSnackbar
import com.greenbee.findme.presentation.home.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ShelterAnimalsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeShelterLayoutModeUseCase: ObserveShelterLayoutModeUseCase,
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val setShelterLayoutModeUseCase: SetShelterLayoutModeUseCase,
    private val getAbandonedAnimalsUseCase: GetAbandonedAnimalsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val restoreFavoriteUseCase: RestoreFavoriteUseCase,
) : ViewModel() {

    private val careRegNo: String = savedStateHandle.get<String>("careRegNo").orEmpty()
    private val shelterName: String = savedStateHandle.get<String>("shelterName").orEmpty()

    private val _uiState = MutableStateFlow(
        ShelterAnimalsUiState(shelterName = shelterName),
    )
    val uiState: StateFlow<ShelterAnimalsUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var isRequestInFlight = false
    private var rawFavorites: List<FavoriteAnimal> = emptyList()
    private var pendingUndoFavorite: FavoriteAnimal? = null

    init {
        viewModelScope.launch {
            observeShelterLayoutModeUseCase().collect { layoutMode ->
                _uiState.update { it.copy(layoutMode = layoutMode) }
            }
        }
        viewModelScope.launch {
            observeFavoritesUseCase().collect { favorites ->
                rawFavorites = favorites
                _uiState.update {
                    it.copy(favoriteDesertionNos = favorites.map { favorite -> favorite.desertionNo }.toSet())
                }
            }
        }
        loadAnimals(reset = true)
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

    fun toggleLayoutMode() {
        val nextMode = when (_uiState.value.layoutMode) {
            HomeLayoutMode.GRID -> HomeLayoutMode.LIST
            HomeLayoutMode.LIST -> HomeLayoutMode.COVER
            HomeLayoutMode.COVER -> HomeLayoutMode.GRID
        }
        viewModelScope.launch {
            setShelterLayoutModeUseCase(nextMode)
        }
    }

    private fun loadAnimals(reset: Boolean, fromPullRefresh: Boolean = false) {
        if (careRegNo.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isEmpty = true,
                    errorMessage = null,
                )
            }
            return
        }
        if (isRequestInFlight) return
        isRequestInFlight = true

        val page = if (reset) 1 else currentPage + 1
        val currentState = _uiState.value

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
                getAbandonedAnimalsUseCase(
                    AbandonedAnimalFilter(
                        pageNo = page,
                        numOfRows = PAGE_SIZE,
                        careRegNo = careRegNo,
                    ),
                )
            }.onSuccess { result ->
                currentPage = page
                val newAnimals = result.items.map { it.toUiModel() }
                val mergedAnimals = (if (reset) newAnimals else currentState.animals + newAnimals)
                    .distinctBy { it.desertionNo }
                val canLoadMore = mergedAnimals.size < result.totalCount

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        animals = mergedAnimals,
                        totalCount = result.totalCount,
                        canLoadMore = canLoadMore,
                        isEmpty = mergedAnimals.isEmpty(),
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

    private companion object {
        const val PAGE_SIZE = 20
    }
}
