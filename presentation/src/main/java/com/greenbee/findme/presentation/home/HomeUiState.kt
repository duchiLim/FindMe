package com.greenbee.findme.presentation.home

data class HomeUiState(
    val isLoading: Boolean = true,
    val totalCount: Int = 0,
    val previewNames: List<String> = emptyList(),
    val errorMessage: String? = null,
)
