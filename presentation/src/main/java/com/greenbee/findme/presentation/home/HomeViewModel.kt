package com.greenbee.findme.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.model.AbandonedAnimalFilter
import com.greenbee.findme.domain.model.AbandonmentState
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.domain.model.UpKind
import com.greenbee.findme.domain.usecase.GetAbandonedAnimalsUseCase
import com.greenbee.findme.domain.usecase.GetSidoListUseCase
import com.greenbee.findme.domain.usecase.GetSigunguListUseCase
import com.greenbee.findme.presentation.home.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAbandonedAnimalsUseCase: GetAbandonedAnimalsUseCase,
    private val getSidoListUseCase: GetSidoListUseCase,
    private val getSigunguListUseCase: GetSigunguListUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var isRequestInFlight = false

    init {
        loadSidoList()
        loadAnimals(reset = true)
    }

    fun onStateSelected(state: AbandonmentState) {
        val nextState = if (_uiState.value.selectedState == state) null else state
        _uiState.update { it.copy(selectedState = nextState) }
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
                draftSido = null,
                draftSigungu = null,
                draftUpKind = null,
                sigunguList = emptyList(),
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

        val page = if (reset) 1 else currentPage + 1
        val currentState = _uiState.value

        viewModelScope.launch {
            isRequestInFlight = true
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
                val mergedAnimals = if (reset) newAnimals else currentState.animals + newAnimals
                val canLoadMore = mergedAnimals.size < result.totalCount

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isFilterLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        animals = mergedAnimals,
                        totalCount = result.totalCount,
                        filterSummary = buildFilterSummary(
                            upKind = it.selectedUpKind,
                            state = it.selectedState,
                            sido = it.selectedSido,
                            sigungu = it.selectedSigungu,
                        ),
                        canLoadMore = canLoadMore,
                        isEmpty = mergedAnimals.isEmpty(),
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isFilterLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        errorMessage = error.message ?: "유기동물 정보를 불러오지 못했습니다.",
                    )
                }
            }

            isRequestInFlight = false
        }
    }

    private fun buildFilter(page: Int, state: HomeUiState): AbandonedAnimalFilter =
        AbandonedAnimalFilter(
            pageNo = page,
            numOfRows = PAGE_SIZE,
            upKind = state.selectedUpKind,
            state = state.selectedState,
            sidoCode = state.selectedSido?.code,
            sigunguCode = state.selectedSigungu?.code,
        )

    private fun buildFilterSummary(
        upKind: UpKind?,
        state: AbandonmentState?,
        sido: Region?,
        sigungu: Region?,
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
        add("최근 30일")
    }.joinToString(separator = " · ")

    private fun hasActiveFilters(state: HomeUiState): Boolean =
        state.selectedUpKind != null ||
            state.selectedState != null ||
            state.selectedSido != null ||
            state.selectedSigungu != null

    private companion object {
        const val PAGE_SIZE = 20
    }
}
