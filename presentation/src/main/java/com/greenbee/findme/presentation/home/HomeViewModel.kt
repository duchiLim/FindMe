package com.greenbee.findme.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.model.AbandonedAnimalFilter
import com.greenbee.findme.domain.model.AbandonmentState
import com.greenbee.findme.domain.model.UpKind
import com.greenbee.findme.domain.usecase.GetAbandonedAnimalsUseCase
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
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var isRequestInFlight = false

    init {
        loadAnimals(reset = true)
    }

    fun onUpKindSelected(upKind: UpKind?) {
        if (_uiState.value.selectedUpKind == upKind) return
        _uiState.update { it.copy(selectedUpKind = upKind) }
        loadAnimals(reset = true)
    }

    fun onStateSelected(state: AbandonmentState) {
        val nextState = if (_uiState.value.selectedState == state) null else state
        _uiState.update { it.copy(selectedState = nextState) }
        loadAnimals(reset = true)
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.canLoadMore || isRequestInFlight) return
        loadAnimals(reset = false)
    }

    fun clearFilters() {
        if (_uiState.value.selectedUpKind == null && _uiState.value.selectedState == null) {
            retry()
            return
        }
        _uiState.update { it.copy(selectedUpKind = null, selectedState = null) }
        loadAnimals(reset = true)
    }

    fun retry() {
        loadAnimals(reset = _uiState.value.animals.isEmpty())
    }

    fun refresh() {
        if (isRequestInFlight || _uiState.value.isRefreshing) return
        loadAnimals(reset = true, fromPullRefresh = true)
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
                        isRefreshing = false,
                        isLoadingMore = false,
                        animals = mergedAnimals,
                        totalCount = result.totalCount,
                        filterSummary = buildFilterSummary(it.selectedUpKind, it.selectedState),
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

    private fun buildFilter(page: Int, state: HomeUiState): AbandonedAnimalFilter =
        AbandonedAnimalFilter(
            pageNo = page,
            numOfRows = PAGE_SIZE,
            upKind = state.selectedUpKind,
            state = state.selectedState,
        )

    private fun buildFilterSummary(
        upKind: UpKind?,
        state: AbandonmentState?,
    ): String = buildList {
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

    private companion object {
        const val PAGE_SIZE = 20
    }
}
