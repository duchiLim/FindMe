package com.greenbee.findme.presentation.shelter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.domain.usecase.GetShelterListUseCase
import com.greenbee.findme.domain.usecase.GetSidoListUseCase
import com.greenbee.findme.domain.usecase.GetSigunguListUseCase
import com.greenbee.findme.presentation.shelter.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ShelterViewModel @Inject constructor(
    private val getShelterListUseCase: GetShelterListUseCase,
    private val getSidoListUseCase: GetSidoListUseCase,
    private val getSigunguListUseCase: GetSigunguListUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShelterUiState())
    val uiState: StateFlow<ShelterUiState> = _uiState.asStateFlow()

    init {
        loadSidoList()
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

    fun onResetRegionDraft() {
        _uiState.update {
            it.copy(
                draftSido = null,
                draftSigungu = null,
                sigunguList = emptyList(),
            )
        }
    }

    fun onApplyRegionFilter() {
        val draftSido = _uiState.value.draftSido ?: return
        val draftSigungu = _uiState.value.draftSigungu ?: return

        _uiState.update {
            it.copy(
                selectedSido = draftSido,
                selectedSigungu = draftSigungu,
                isRegionFilterSheetVisible = false,
                hasAppliedRegion = true,
            )
        }
        loadShelters()
    }

    fun retry() {
        if (!_uiState.value.hasAppliedRegion) {
            onRegionFilterClick()
            return
        }
        loadShelters()
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

    private fun loadShelters() {
        val state = _uiState.value
        val sidoCode = state.selectedSido?.code ?: return
        val sigunguCode = state.selectedSigungu?.code ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isShelterListLoading = true,
                    errorMessage = null,
                    isEmpty = false,
                )
            }

            runCatching {
                getShelterListUseCase(
                    sidoCode = sidoCode,
                    sigunguCode = sigunguCode,
                )
            }.onSuccess { result ->
                val shelters = result.items.map { it.toUiModel() }.distinctBy { it.careRegNo }
                _uiState.update {
                    it.copy(
                        isShelterListLoading = false,
                        shelters = shelters,
                        totalCount = result.totalCount,
                        isEmpty = shelters.isEmpty(),
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isShelterListLoading = false,
                        errorMessage = error.message ?: "보호소 정보를 불러오지 못했습니다.",
                    )
                }
            }
        }
    }
}
