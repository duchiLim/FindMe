package com.greenbee.findme.presentation.shelter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.domain.model.Shelter
import com.greenbee.findme.domain.usecase.GetCurrentLocationUseCase
import com.greenbee.findme.domain.usecase.GetShelterListUseCase
import com.greenbee.findme.domain.usecase.GetSigunguListUseCase
import com.greenbee.findme.domain.usecase.GetSidoListUseCase
import com.greenbee.findme.domain.usecase.ObserveShelterFavoriteCareRegNosUseCase
import com.greenbee.findme.domain.usecase.ObserveShelterFavoritesUseCase
import com.greenbee.findme.domain.usecase.ResolveRegionFromLocationUseCase
import com.greenbee.findme.domain.usecase.SortSheltersByDistanceUseCase
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
class ShelterViewModel @Inject constructor(
    private val getShelterListUseCase: GetShelterListUseCase,
    private val getSidoListUseCase: GetSidoListUseCase,
    private val getSigunguListUseCase: GetSigunguListUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val resolveRegionFromLocationUseCase: ResolveRegionFromLocationUseCase,
    private val sortSheltersByDistanceUseCase: SortSheltersByDistanceUseCase,
    private val observeShelterFavoritesUseCase: ObserveShelterFavoritesUseCase,
    private val observeShelterFavoriteCareRegNosUseCase: ObserveShelterFavoriteCareRegNosUseCase,
    private val toggleShelterFavoriteUseCase: ToggleShelterFavoriteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShelterUiState())
    val uiState: StateFlow<ShelterUiState> = _uiState.asStateFlow()

    init {
        loadSidoList()
        observeFavorites()
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

    fun onListModeSelected(mode: ShelterListMode) {
        _uiState.update { it.copy(listMode = mode) }
    }

    fun onSortModeSelected(mode: ShelterSortMode) {
        _uiState.update { it.copy(sortMode = mode) }
        if (mode == ShelterSortMode.DISTANCE) {
            applyDistanceSorting()
        }
    }

    fun onLocationPermissionGranted() {
        findNearbyShelters()
    }

    fun onLocationPermissionDenied() {
        _uiState.update {
            it.copy(locationMessage = "위치 권한이 필요합니다.")
        }
    }

    fun onLocationMessageShown() {
        _uiState.update { it.copy(locationMessage = null) }
    }

    fun retry() {
        if (!_uiState.value.hasAppliedRegion && _uiState.value.listMode == ShelterListMode.ALL) {
            onRegionFilterClick()
            return
        }
        if (_uiState.value.listMode == ShelterListMode.ALL) {
            loadShelters()
        }
    }

    fun refresh() {
        if (_uiState.value.listMode != ShelterListMode.ALL) return
        if (!_uiState.value.hasAppliedRegion || _uiState.value.isShelterListLoading) return
        loadShelters(fromPullRefresh = true)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "") }
    }

    fun toggleFavorite(shelter: com.greenbee.findme.presentation.shelter.model.ShelterUiModel) {
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
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            observeShelterFavoriteCareRegNosUseCase().collect { careRegNos ->
                _uiState.update { it.copy(favoriteCareRegNos = careRegNos) }
            }
        }
        viewModelScope.launch {
            observeShelterFavoritesUseCase().collect { favorites ->
                _uiState.update {
                    it.copy(
                        favoriteShelters = favorites.map { favorite ->
                            favorite.toUiModel(isFavorite = true)
                        },
                    )
                }
            }
        }
    }

    private fun findNearbyShelters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLocationLoading = true, locationMessage = null) }

            val location = runCatching { getCurrentLocationUseCase() }.getOrNull()
            if (location == null) {
                _uiState.update {
                    it.copy(
                        isLocationLoading = false,
                        locationMessage = "현재 위치를 가져오지 못했습니다.",
                    )
                }
                return@launch
            }

            val state = _uiState.value
            if (!state.hasAppliedRegion) {
                val resolvedRegion = resolveRegionFromLocationUseCase(
                    location = location,
                    sidoList = state.sidoList,
                    sigunguProvider = { sidoCode ->
                        runCatching { getSigunguListUseCase(sidoCode).items }.getOrDefault(emptyList())
                    },
                )
                if (resolvedRegion == null) {
                    _uiState.update {
                        it.copy(
                            isLocationLoading = false,
                            locationMessage = "현재 위치의 지역을 찾지 못했습니다. 지역을 직접 선택해 주세요.",
                        )
                    }
                    return@launch
                }
                val (sido, sigungu) = resolvedRegion
                _uiState.update {
                    it.copy(
                        selectedSido = sido,
                        selectedSigungu = sigungu,
                        hasAppliedRegion = true,
                        userLocation = location,
                        sortMode = ShelterSortMode.DISTANCE,
                    )
                }
                loadShelters(userLocation = location, applyDistanceSort = true)
                return@launch
            }

            _uiState.update {
                it.copy(
                    userLocation = location,
                    sortMode = ShelterSortMode.DISTANCE,
                )
            }
            applyDistanceSorting(location)
            _uiState.update { it.copy(isLocationLoading = false) }
        }
    }

    private fun applyDistanceSorting(forcedLocation: com.greenbee.findme.domain.model.GeoLocation? = null) {
        val userLocation = forcedLocation ?: _uiState.value.userLocation ?: return
        val shelters = _uiState.value.shelters.map { uiModel ->
            Shelter(
                careRegNo = uiModel.careRegNo,
                name = uiModel.name,
                orgName = uiModel.orgName,
                tel = uiModel.tel,
                address = uiModel.address,
            )
        }
        if (shelters.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLocationLoading = true) }
            runCatching {
                sortSheltersByDistanceUseCase(
                    shelters = shelters,
                    userLocation = userLocation,
                )
            }.onSuccess { sorted ->
                val favoriteCareRegNos = _uiState.value.favoriteCareRegNos
                _uiState.update {
                    it.copy(
                        isLocationLoading = false,
                        shelters = sorted.map { item ->
                            item.shelter.toUiModel(
                                distanceMeters = item.distanceMeters,
                                location = item.location,
                                isFavorite = favoriteCareRegNos.contains(item.shelter.careRegNo),
                            )
                        },
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLocationLoading = false,
                        locationMessage = "거리 계산에 실패했습니다.",
                    )
                }
            }
        }
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

    private fun loadShelters(
        fromPullRefresh: Boolean = false,
        userLocation: com.greenbee.findme.domain.model.GeoLocation? = null,
        applyDistanceSort: Boolean = false,
    ) {
        val state = _uiState.value
        val sidoCode = state.selectedSido?.code ?: return
        val sigunguCode = state.selectedSigungu?.code ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isShelterListLoading = !fromPullRefresh,
                    isRefreshing = fromPullRefresh,
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
                val favoriteCareRegNos = _uiState.value.favoriteCareRegNos
                val shelters = result.items
                    .map { shelter ->
                        shelter.toUiModel(
                            isFavorite = favoriteCareRegNos.contains(shelter.careRegNo),
                        )
                    }
                    .distinctBy { it.careRegNo }
                _uiState.update {
                    it.copy(
                        isShelterListLoading = false,
                        isRefreshing = false,
                        shelters = shelters,
                        totalCount = result.totalCount,
                        isEmpty = shelters.isEmpty(),
                        errorMessage = null,
                        userLocation = userLocation ?: it.userLocation,
                    )
                }
                if (applyDistanceSort) {
                    applyDistanceSorting(userLocation)
                } else {
                    _uiState.update { it.copy(isLocationLoading = false) }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isShelterListLoading = false,
                        isRefreshing = false,
                        isLocationLoading = false,
                        errorMessage = error.message ?: "보호소 정보를 불러오지 못했습니다.",
                    )
                }
            }
        }
    }
}
