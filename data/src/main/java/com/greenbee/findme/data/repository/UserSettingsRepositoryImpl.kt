package com.greenbee.findme.data.repository

import com.greenbee.findme.data.settings.UserSettingsDataSource
import com.greenbee.findme.domain.repository.UserSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingsRepositoryImpl @Inject constructor(
    private val dataSource: UserSettingsDataSource,
) : UserSettingsRepository {

    override fun isNotificationEnabled(): Boolean = dataSource.isNotificationEnabled()

    override fun setNotificationEnabled(enabled: Boolean) {
        dataSource.setNotificationEnabled(enabled)
    }

    override fun isRecentViewingEnabled(): Boolean = dataSource.isRecentViewingEnabled()

    override fun setRecentViewingEnabled(enabled: Boolean) {
        dataSource.setRecentViewingEnabled(enabled)
    }
}
