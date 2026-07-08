package com.greenbee.findme.presentation.my

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenbee.findme.domain.usecase.ObserveRecentlyViewedUseCase
import com.greenbee.findme.presentation.home.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MyViewModel @Inject constructor(
    @ApplicationContext context: Context,
    observeRecentlyViewedUseCase: ObserveRecentlyViewedUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()

    init {
        val versionName = runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull().orEmpty()
        val versionCode = runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode.toInt()
        }.getOrDefault(0)

        _uiState.update {
            it.copy(
                appVersionName = versionName,
                appVersionCode = versionCode,
            )
        }

        viewModelScope.launch {
            observeRecentlyViewedUseCase().collect { recentlyViewed ->
                _uiState.update {
                    it.copy(
                        recentAnimals = recentlyViewed.map { item -> item.animal.toUiModel() },
                        isLoading = false,
                    )
                }
            }
        }
    }
}
