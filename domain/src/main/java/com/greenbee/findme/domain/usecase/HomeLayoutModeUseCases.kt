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

class ObserveRecentLayoutModeUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    operator fun invoke(): Flow<HomeLayoutMode> = repository.observeRecentLayoutMode()
}

class SetRecentLayoutModeUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    suspend operator fun invoke(mode: HomeLayoutMode) {
        repository.setRecentLayoutMode(mode)
    }
}

class ObserveShelterLayoutModeUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    operator fun invoke(): Flow<HomeLayoutMode> = repository.observeShelterLayoutMode()
}

class SetShelterLayoutModeUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    suspend operator fun invoke(mode: HomeLayoutMode) {
        repository.setShelterLayoutMode(mode)
    }
}

class ObserveFavoriteLayoutModeUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    operator fun invoke(): Flow<HomeLayoutMode> = repository.observeFavoriteLayoutMode()
}

class SetFavoriteLayoutModeUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    suspend operator fun invoke(mode: HomeLayoutMode) {
        repository.setFavoriteLayoutMode(mode)
    }
}
