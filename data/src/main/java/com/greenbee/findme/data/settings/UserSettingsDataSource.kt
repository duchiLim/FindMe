package com.greenbee.findme.data.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingsDataSource @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isNotificationEnabled(): Boolean =
        prefs.getBoolean(KEY_NOTIFICATION_ENABLED, false)

    fun setNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply()
    }

    fun isRecentViewingEnabled(): Boolean =
        prefs.getBoolean(KEY_RECENT_VIEWING_ENABLED, true)

    fun setRecentViewingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_RECENT_VIEWING_ENABLED, enabled).apply()
    }

    private companion object {
        const val PREFS_NAME = "findme_user_settings"
        const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
        const val KEY_RECENT_VIEWING_ENABLED = "recent_viewing_enabled"
    }
}
