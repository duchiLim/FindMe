package com.greenbee.findme.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.greenbee.findme.domain.model.HomeLayoutMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "findme_user_preferences",
)

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    val homeLayoutMode: Flow<HomeLayoutMode> = context.userPreferencesDataStore.data.map { preferences ->
        HomeLayoutMode.fromStored(preferences[KEY_HOME_LAYOUT_MODE])
    }

    suspend fun setHomeLayoutMode(mode: HomeLayoutMode) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[KEY_HOME_LAYOUT_MODE] = mode.name
        }
    }

    val recentLayoutMode: Flow<HomeLayoutMode> = context.userPreferencesDataStore.data.map { preferences ->
        HomeLayoutMode.fromStored(preferences[KEY_RECENT_LAYOUT_MODE])
    }

    suspend fun setRecentLayoutMode(mode: HomeLayoutMode) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[KEY_RECENT_LAYOUT_MODE] = mode.name
        }
    }

    val shelterLayoutMode: Flow<HomeLayoutMode> = context.userPreferencesDataStore.data.map { preferences ->
        HomeLayoutMode.fromStored(preferences[KEY_SHELTER_LAYOUT_MODE])
    }

    suspend fun setShelterLayoutMode(mode: HomeLayoutMode) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[KEY_SHELTER_LAYOUT_MODE] = mode.name
        }
    }

    val favoriteLayoutMode: Flow<HomeLayoutMode> = context.userPreferencesDataStore.data.map { preferences ->
        HomeLayoutMode.fromStored(preferences[KEY_FAVORITE_LAYOUT_MODE])
    }

    suspend fun setFavoriteLayoutMode(mode: HomeLayoutMode) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[KEY_FAVORITE_LAYOUT_MODE] = mode.name
        }
    }

    private companion object {
        val KEY_HOME_LAYOUT_MODE = stringPreferencesKey("home_layout_mode")
        val KEY_RECENT_LAYOUT_MODE = stringPreferencesKey("recent_layout_mode")
        val KEY_SHELTER_LAYOUT_MODE = stringPreferencesKey("shelter_layout_mode")
        val KEY_FAVORITE_LAYOUT_MODE = stringPreferencesKey("favorite_layout_mode")
    }
}
