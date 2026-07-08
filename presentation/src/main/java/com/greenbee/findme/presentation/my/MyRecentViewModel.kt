package com.greenbee.findme.presentation.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.domain.usecase.ClearRecentlyViewedUseCase
import com.greenbee.findme.domain.usecase.ObserveAllRecentlyViewedUseCase
import com.greenbee.findme.domain.usecase.ObserveRecentLayoutModeUseCase
import com.greenbee.findme.domain.usecase.RefreshRecentSnapshotsUseCase
import com.greenbee.findme.domain.usecase.RemoveRecentlyViewedUseCase
import com.greenbee.findme.domain.usecase.SetRecentLayoutModeUseCase
import com.greenbee.findme.presentation.my.model.toRecentUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MyRecentViewModel @Inject constructor(
    observeAllRecentlyViewedUseCase: ObserveAllRecentlyViewedUseCase,
    observeRecentLayoutModeUseCase: ObserveRecentLayoutModeUseCase,
    private val setRecentLayoutModeUseCase: SetRecentLayoutModeUseCase,
    private val clearRecentlyViewedUseCase: ClearRecentlyViewedUseCase,
    private val removeRecentlyViewedUseCase: RemoveRecentlyViewedUseCase,
    private val refreshRecentSnapshotsUseCase: RefreshRecentSnapshotsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyRecentUiState())
    val uiState: StateFlow<MyRecentUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeRecentLayoutModeUseCase().collect { layoutMode ->
                _uiState.update { it.copy(layoutMode = layoutMode) }
            }
        }
        viewModelScope.launch {
            observeAllRecentlyViewedUseCase().collect { recentlyViewed ->
                val items = recentlyViewed.map { it.toRecentUiModel() }
                _uiState.update {
                    it.copy(
                        recentAnimals = items,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun refreshSnapshots() {
        val desertionNos = _uiState.value.recentAnimals.map { it.desertionNo }
        if (desertionNos.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            runCatching { refreshRecentSnapshotsUseCase(desertionNos) }
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun onClearAllClick() {
        _uiState.update { it.copy(showClearConfirmDialog = true) }
    }

    fun onClearAllDismiss() {
        _uiState.update { it.copy(showClearConfirmDialog = false) }
    }

    fun onClearAllConfirm() {
        viewModelScope.launch {
            clearRecentlyViewedUseCase()
            _uiState.update { it.copy(showClearConfirmDialog = false) }
        }
    }

    fun onRemoveItem(desertionNo: String) {
        viewModelScope.launch {
            removeRecentlyViewedUseCase(desertionNo)
        }
    }

    fun toggleLayoutMode() {
        val nextMode = when (_uiState.value.layoutMode) {
            HomeLayoutMode.GRID -> HomeLayoutMode.LIST
            HomeLayoutMode.LIST -> HomeLayoutMode.COVER
            HomeLayoutMode.COVER -> HomeLayoutMode.GRID
        }
        viewModelScope.launch {
            setRecentLayoutModeUseCase(nextMode)
        }
    }
}
