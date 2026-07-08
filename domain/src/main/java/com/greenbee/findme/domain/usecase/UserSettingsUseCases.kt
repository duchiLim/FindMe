package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.repository.UserSettingsRepository
import javax.inject.Inject

class GetUserSettingsUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    operator fun invoke(): UserSettingsSnapshot = UserSettingsSnapshot(
        isNotificationEnabled = repository.isNotificationEnabled(),
        isRecentViewingEnabled = repository.isRecentViewingEnabled(),
    )
}

class SetNotificationEnabledUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    operator fun invoke(enabled: Boolean) {
        repository.setNotificationEnabled(enabled)
    }
}

class SetRecentViewingEnabledUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
    private val clearRecentlyViewedUseCase: ClearRecentlyViewedUseCase,
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setRecentViewingEnabled(enabled)
        if (!enabled) {
            clearRecentlyViewedUseCase()
        }
    }
}

data class UserSettingsSnapshot(
    val isNotificationEnabled: Boolean,
    val isRecentViewingEnabled: Boolean,
)
