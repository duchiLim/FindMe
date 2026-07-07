package com.greenbee.findme.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.model.AbandonedAnimalFilter
import com.greenbee.findme.domain.usecase.GetAbandonedAnimalsUseCase
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

    init {
        loadAbandonedAnimals()
    }

    private fun loadAbandonedAnimals() {
        viewModelScope.launch {
            runCatching {
                getAbandonedAnimalsUseCase(
                    AbandonedAnimalFilter(numOfRows = 10),
                )
            }.onSuccess { result ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalCount = result.totalCount,
                        previewNames = result.items.map { animal -> animal.kindName }.take(3),
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "유기동물 정보를 불러오지 못했습니다.",
                    )
                }
            }
        }
    }
}
