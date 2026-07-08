package com.greenbee.findme.presentation.shelter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.model.Shelter
import com.greenbee.findme.domain.usecase.GeocodeAddressUseCase
import com.greenbee.findme.domain.usecase.GetShelterDetailUseCase
import com.greenbee.findme.domain.usecase.ObserveIsShelterFavoriteUseCase
import com.greenbee.findme.domain.usecase.ShelterFavoriteToggleResult
import com.greenbee.findme.domain.usecase.ToggleShelterFavoriteUseCase
import com.greenbee.findme.presentation.shelter.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ShelterDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getShelterDetailUseCase: GetShelterDetailUseCase,
    private val geocodeAddressUseCase: GeocodeAddressUseCase,
    private val toggleShelterFavoriteUseCase: ToggleShelterFavoriteUseCase,
    private val observeIsShelterFavoriteUseCase: ObserveIsShelterFavoriteUseCase,
) : ViewModel() {

    private val careRegNo: String = savedStateHandle.get<String>("careRegNo").orEmpty()

    private val _uiState = MutableStateFlow(ShelterDetailUiState())
    val uiState: StateFlow<ShelterDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
        if (careRegNo.isNotBlank()) {
            viewModelScope.launch {
                observeIsShelterFavoriteUseCase(careRegNo).collect { isFavorite ->
                    _uiState.update { it.copy(isFavorite = isFavorite) }
                }
            }
        }
    }

    fun retry() {
        loadDetail()
    }

    fun toggleFavorite() {
        val shelter = _uiState.value.shelter ?: return
        viewModelScope.launch {
            runCatching {
                toggleShelterFavoriteUseCase(
                    Shelter(
                        careRegNo = shelter.careRegNo,
                        name = shelter.name,
                        orgName = shelter.orgName,
                        tel = shelter.tel,
                        address = shelter.address,
                    ),
                )
            }.onSuccess { result ->
                _uiState.update {
                    it.copy(
                        favoriteMessage = when (result) {
                            ShelterFavoriteToggleResult.ADDED -> ShelterFavoriteMessage.ADDED
                            ShelterFavoriteToggleResult.REMOVED -> ShelterFavoriteMessage.REMOVED
                        },
                    )
                }
            }
        }
    }

    fun onFavoriteMessageShown() {
        _uiState.update { it.copy(favoriteMessage = null) }
    }

    private fun loadDetail() {
        if (careRegNo.isBlank()) {
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
            _uiState.update {
                it.copy(
                    isLoading = it.shelter == null,
                    errorMessage = null,
                    isNotFound = false,
                )
            }

            runCatching { getShelterDetailUseCase(careRegNo) }
                .onSuccess { shelter ->
                    if (shelter != null) {
                        val uiModel = shelter.toUiModel(isFavorite = _uiState.value.isFavorite)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                shelter = uiModel,
                                errorMessage = null,
                                isNotFound = false,
                            )
                        }
                        loadMapLocation(uiModel.address)
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isNotFound = true,
                                errorMessage = null,
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "보호소 정보를 불러오지 못했습니다.",
                        )
                    }
                }
        }
    }

    private fun loadMapLocation(address: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isMapLoading = true) }
            val location = runCatching { geocodeAddressUseCase(address) }.getOrNull()
            _uiState.update {
                it.copy(
                    isMapLoading = false,
                    mapLocation = location,
                )
            }
        }
    }
}
