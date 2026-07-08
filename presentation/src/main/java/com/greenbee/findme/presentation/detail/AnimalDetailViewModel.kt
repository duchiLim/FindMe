package com.greenbee.findme.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.usecase.FavoriteToggleResult
import com.greenbee.findme.domain.usecase.FindShelterByNameUseCase
import com.greenbee.findme.domain.usecase.GetAbandonedAnimalDetailUseCase
import com.greenbee.findme.domain.usecase.ObserveIsFavoriteUseCase
import com.greenbee.findme.domain.usecase.RecordRecentlyViewedUseCase
import com.greenbee.findme.domain.usecase.ToggleFavoriteUseCase
import com.greenbee.findme.presentation.detail.model.toDetailUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AnimalDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAbandonedAnimalDetailUseCase: GetAbandonedAnimalDetailUseCase,
    private val observeIsFavoriteUseCase: ObserveIsFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val recordRecentlyViewedUseCase: RecordRecentlyViewedUseCase,
    private val findShelterByNameUseCase: FindShelterByNameUseCase,
) : ViewModel() {

    private val desertionNo: String = savedStateHandle.get<String>("desertionNo").orEmpty()

    private val _uiState = MutableStateFlow(AnimalDetailUiState())
    val uiState: StateFlow<AnimalDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
        if (desertionNo.isNotBlank()) {
            viewModelScope.launch {
                observeIsFavoriteUseCase(desertionNo).collect { isFavorite ->
                    _uiState.update { it.copy(isFavorite = isFavorite) }
                }
            }
        }
    }

    fun retry() {
        loadDetail(forceLoading = true)
    }

    fun toggleFavorite() {
        if (desertionNo.isBlank()) return

        viewModelScope.launch {
            runCatching { toggleFavoriteUseCase(desertionNo) }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            favoriteMessage = when (result) {
                                FavoriteToggleResult.ADDED -> FavoriteMessage.ADDED
                                FavoriteToggleResult.REMOVED -> FavoriteMessage.REMOVED
                            },
                        )
                    }
                }
        }
    }

    fun onFavoriteMessageShown() {
        _uiState.update { it.copy(favoriteMessage = null) }
    }

    fun onShelterNameClick() {
        val animal = _uiState.value.animal ?: return
        if (animal.shelterName.isBlank() || animal.shelterName == "보호소 정보 없음") return

        viewModelScope.launch {
            _uiState.update { it.copy(isShelterLookupLoading = true, showShelterNotFound = false) }
            runCatching { findShelterByNameUseCase(animal.shelterName) }
                .onSuccess { shelter ->
                    _uiState.update {
                        it.copy(
                            isShelterLookupLoading = false,
                            navigateToShelterCareRegNo = shelter?.careRegNo,
                            showShelterNotFound = shelter == null,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isShelterLookupLoading = false,
                            showShelterNotFound = true,
                        )
                    }
                }
        }
    }

    fun onShelterNavigationHandled() {
        _uiState.update { it.copy(navigateToShelterCareRegNo = null) }
    }

    fun onShelterNotFoundShown() {
        _uiState.update { it.copy(showShelterNotFound = false) }
    }

    private fun loadDetail(forceLoading: Boolean = false) {
        if (desertionNo.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isNotFound = true,
                    errorMessage = null,
                )
            }
            return
        }

        viewModelScope.launch {
            val cachedAnimal = getAbandonedAnimalDetailUseCase.getCached(desertionNo)
            if (cachedAnimal != null && !forceLoading) {
                _uiState.update {
                    it.copy(
                        animal = cachedAnimal.toDetailUiModel(),
                        isLoading = false,
                        isNotFound = false,
                        errorMessage = null,
                    )
                }
                recordRecentlyViewed()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = cachedAnimal == null,
                        isRefreshing = cachedAnimal != null,
                        errorMessage = null,
                        isNotFound = false,
                    )
                }
            }

            runCatching { getAbandonedAnimalDetailUseCase(desertionNo) }
                .onSuccess { animal ->
                    if (animal != null) {
                        _uiState.update {
                            it.copy(
                                animal = animal.toDetailUiModel(),
                                isLoading = false,
                                isRefreshing = false,
                                isNotFound = false,
                                errorMessage = null,
                            )
                        }
                        recordRecentlyViewed()
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                isNotFound = cachedAnimal == null && it.animal == null,
                                errorMessage = null,
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = if (it.animal == null) {
                                error.message ?: "동물 정보를 불러오지 못했습니다."
                            } else {
                                null
                            },
                        )
                    }
                }
        }
    }

    private fun recordRecentlyViewed() {
        viewModelScope.launch {
            runCatching { recordRecentlyViewedUseCase(desertionNo) }
        }
    }
}
