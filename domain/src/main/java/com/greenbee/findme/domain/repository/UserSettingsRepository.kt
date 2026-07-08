package com.greenbee.findme.domain.repository

interface UserSettingsRepository {
    fun isNotificationEnabled(): Boolean

    fun setNotificationEnabled(enabled: Boolean)

    fun isRecentViewingEnabled(): Boolean

    fun setRecentViewingEnabled(enabled: Boolean)
}
