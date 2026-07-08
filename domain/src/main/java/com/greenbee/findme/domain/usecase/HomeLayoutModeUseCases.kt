package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.domain.repository.UserSettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveHomeLayoutModeUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    operator fun invoke(): Flow<HomeLayoutMode> = repository.observeHomeLayoutMode()
}

class SetHomeLayoutModeUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    suspend operator fun invoke(mode: HomeLayoutMode) {
        repository.setHomeLayoutMode(mode)
    }
}
