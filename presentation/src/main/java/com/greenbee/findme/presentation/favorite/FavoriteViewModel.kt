package com.greenbee.findme.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.usecase.ObserveFavoritesUseCase
import com.greenbee.findme.domain.usecase.RefreshFavoriteSnapshotsUseCase
import com.greenbee.findme.domain.usecase.RemoveFavoriteUseCase
import com.greenbee.findme.presentation.home.model.toUiModel
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
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val refreshFavoriteSnapshotsUseCase: RefreshFavoriteSnapshotsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    private var latestDesertionNos: List<String> = emptyList()

    init {
        viewModelScope.launch {
            observeFavoritesUseCase().collect { favorites ->
                latestDesertionNos = favorites.map { it.desertionNo }
                val uiModels = favorites.map { it.animal.toUiModel() }
                _uiState.update {
                    it.copy(
                        favorites = uiModels,
                        isLoading = false,
                        isEmpty = uiModels.isEmpty(),
                    )
                }
            }
        }
    }

    fun refreshSnapshots() {
        if (latestDesertionNos.isEmpty() || _uiState.value.isRefreshing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            runCatching { refreshFavoriteSnapshotsUseCase(latestDesertionNos) }
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun removeFavorite(desertionNo: String) {
        viewModelScope.launch {
            removeFavoriteUseCase(desertionNo)
        }
    }
}
