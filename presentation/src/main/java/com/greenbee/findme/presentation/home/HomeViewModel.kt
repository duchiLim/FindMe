package com.greenbee.findme.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.usecase.GetSampleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSampleUseCase: GetSampleUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadSample()
    }

    private fun loadSample() {
        viewModelScope.launch {
            runCatching { getSampleUseCase() }
                .onSuccess { sample ->
                    _uiState.update {
                        it.copy(message = sample.name, isLoading = false)
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(message = "데이터를 불러오지 못했습니다.", isLoading = false)
                    }
                }
        }
    }
}
