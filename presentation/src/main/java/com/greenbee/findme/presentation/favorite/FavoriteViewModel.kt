package com.greenbee.findme.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.model.FavoriteAnimal
import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.domain.usecase.ObserveFavoriteLayoutModeUseCase
import com.greenbee.findme.domain.usecase.ObserveFavoritesUseCase
import com.greenbee.findme.domain.usecase.RefreshFavoriteSnapshotsUseCase
import com.greenbee.findme.domain.usecase.RemoveEndedFavoritesUseCase
import com.greenbee.findme.domain.usecase.RemoveFavoriteUseCase
import com.greenbee.findme.domain.usecase.RestoreFavoriteUseCase
import com.greenbee.findme.domain.usecase.SetFavoriteLayoutModeUseCase
import com.greenbee.findme.presentation.favorite.model.toFavoriteUiModel
import com.greenbee.findme.presentation.favorite.model.sortedBy
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    observeFavoriteLayoutModeUseCase: ObserveFavoriteLayoutModeUseCase,
    private val setFavoriteLayoutModeUseCase: SetFavoriteLayoutModeUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val restoreFavoriteUseCase: RestoreFavoriteUseCase,
    private val refreshFavoriteSnapshotsUseCase: RefreshFavoriteSnapshotsUseCase,
    private val removeEndedFavoritesUseCase: RemoveEndedFavoritesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    private var rawFavorites: List<FavoriteAnimal> = emptyList()
    private var latestDesertionNos: List<String> = emptyList()

    init {
        viewModelScope.launch {
            observeFavoriteLayoutModeUseCase().collect { layoutMode ->
                _uiState.update { it.copy(layoutMode = layoutMode) }
            }
        }
        viewModelScope.launch {
            observeFavoritesUseCase().collect { favorites ->
                rawFavorites = favorites
                latestDesertionNos = favorites.map { it.desertionNo }
                publishFavorites()
            }
        }
    }

    fun refreshSnapshots() {
        if (latestDesertionNos.isEmpty() || _uiState.value.isRefreshing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, refreshFailedCount = 0) }
            val result = runCatching { refreshFavoriteSnapshotsUseCase(latestDesertionNos) }
                .getOrElse { com.greenbee.findme.domain.usecase.FavoriteRefreshResult(failedCount = latestDesertionNos.size) }
            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    refreshFailedCount = result.failedCount,
                )
            }
        }
    }

    fun setSortOrder(sortOrder: FavoriteSortOrder) {
        _uiState.update { it.copy(sortOrder = sortOrder) }
        publishFavorites()
    }

    fun removeFavorite(desertionNo: String) {
        val favorite = rawFavorites.find { it.desertionNo == desertionNo } ?: return

        viewModelScope.launch {
            removeFavoriteUseCase(desertionNo)
            _uiState.update {
                it.copy(
                    pendingUndoFavorite = favorite,
                    showRemovedSnackbar = true,
                )
            }
        }
    }

    fun undoRemoveFavorite() {
        val favorite = _uiState.value.pendingUndoFavorite ?: return

        viewModelScope.launch {
            restoreFavoriteUseCase(favorite)
            onRemovedSnackbarHandled()
        }
    }

    fun onRemovedSnackbarHandled() {
        _uiState.update {
            it.copy(
                pendingUndoFavorite = null,
                showRemovedSnackbar = false,
            )
        }
    }

    fun onUndoSnackbarShown() {
        onRemovedSnackbarHandled()
    }

    fun clearEndedFavorites() {
        viewModelScope.launch {
            removeEndedFavoritesUseCase()
        }
    }

    fun toggleLayoutMode() {
        val nextMode = when (_uiState.value.layoutMode) {
            HomeLayoutMode.GRID -> HomeLayoutMode.LIST
            HomeLayoutMode.LIST -> HomeLayoutMode.COVER
            HomeLayoutMode.COVER -> HomeLayoutMode.GRID
        }
        viewModelScope.launch {
            setFavoriteLayoutModeUseCase(nextMode)
        }
    }

    private fun publishFavorites() {
        val sortOrder = _uiState.value.sortOrder
        val uiModels = rawFavorites
            .map { it.toFavoriteUiModel() }
            .sortedBy(sortOrder)
        _uiState.update {
            it.copy(
                favorites = uiModels,
                isLoading = false,
                isEmpty = uiModels.isEmpty(),
            )
        }
    }
}
