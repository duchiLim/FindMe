package com.greenbee.findme.domain.repository

import com.greenbee.findme.domain.model.HomeLayoutMode
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    fun isNotificationEnabled(): Boolean

    fun setNotificationEnabled(enabled: Boolean)

    fun isRecentViewingEnabled(): Boolean

    fun setRecentViewingEnabled(enabled: Boolean)

    fun observeHomeLayoutMode(): Flow<HomeLayoutMode>

    suspend fun setHomeLayoutMode(mode: HomeLayoutMode)
}
