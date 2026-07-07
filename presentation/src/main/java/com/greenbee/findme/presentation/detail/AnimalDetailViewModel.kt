package com.greenbee.findme.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.usecase.GetAbandonedAnimalDetailUseCase
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
) : ViewModel() {

    private val desertionNo: String = savedStateHandle.get<String>("desertionNo").orEmpty()

    private val _uiState = MutableStateFlow(AnimalDetailUiState())
    val uiState: StateFlow<AnimalDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun retry() {
        loadDetail(forceLoading = true)
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
}
