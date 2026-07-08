package com.greenbee.findme.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.greenbee.findme.domain.model.HomeDateRange
import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.domain.model.HomeRegionFilter
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.domain.model.UpKind
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    suspend fun getHomeRegionFilter(): HomeRegionFilter {
        val preferences = context.userPreferencesDataStore.data.first()
        val sidoCode = preferences[KEY_HOME_SIDO_CODE]?.takeIf { it.isNotBlank() }
        val sido = sidoCode?.let { code ->
            Region(
                code = code,
                name = preferences[KEY_HOME_SIDO_NAME].orEmpty(),
            )
        }
        val sigunguCode = preferences[KEY_HOME_SIGUNGU_CODE]?.takeIf { it.isNotBlank() }
        val sigungu = sigunguCode?.let { code ->
            Region(
                code = code,
                name = preferences[KEY_HOME_SIGUNGU_NAME].orEmpty(),
                parentCode = preferences[KEY_HOME_SIGUNGU_PARENT_CODE]?.takeIf { it.isNotBlank() },
            )
        }
        return HomeRegionFilter(sido = sido, sigungu = sigungu)
    }

    suspend fun setHomeRegionFilter(filter: HomeRegionFilter) {
        context.userPreferencesDataStore.edit { preferences ->
            val sido = filter.sido
            if (sido == null) {
                preferences.remove(KEY_HOME_SIDO_CODE)
                preferences.remove(KEY_HOME_SIDO_NAME)
            } else {
                preferences[KEY_HOME_SIDO_CODE] = sido.code
                preferences[KEY_HOME_SIDO_NAME] = sido.name
            }

            val sigungu = filter.sigungu
            if (sigungu == null) {
                preferences.remove(KEY_HOME_SIGUNGU_CODE)
                preferences.remove(KEY_HOME_SIGUNGU_NAME)
                preferences.remove(KEY_HOME_SIGUNGU_PARENT_CODE)
            } else {
                preferences[KEY_HOME_SIGUNGU_CODE] = sigungu.code
                preferences[KEY_HOME_SIGUNGU_NAME] = sigungu.name
                val parentCode = sigungu.parentCode
                if (parentCode.isNullOrBlank()) {
                    preferences.remove(KEY_HOME_SIGUNGU_PARENT_CODE)
                } else {
                    preferences[KEY_HOME_SIGUNGU_PARENT_CODE] = parentCode
                }
            }
        }
    }

    suspend fun getHomeUpKindFilter(): UpKind? {
        val preferences = context.userPreferencesDataStore.data.first()
        return UpKind.fromStored(preferences[KEY_HOME_UP_KIND])
    }

    suspend fun setHomeUpKindFilter(upKind: UpKind?) {
        context.userPreferencesDataStore.edit { preferences ->
            if (upKind == null) {
                preferences.remove(KEY_HOME_UP_KIND)
            } else {
                preferences[KEY_HOME_UP_KIND] = upKind.name
            }
        }
    }

    suspend fun getHomeDateRangeFilter(): HomeDateRange {
        val preferences = context.userPreferencesDataStore.data.first()
        return HomeDateRange.fromStored(preferences[KEY_HOME_DATE_RANGE])
    }

    suspend fun setHomeDateRangeFilter(dateRange: HomeDateRange) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[KEY_HOME_DATE_RANGE] = dateRange.name
        }
    }

    private companion object {
        val KEY_HOME_LAYOUT_MODE = stringPreferencesKey("home_layout_mode")
        val KEY_RECENT_LAYOUT_MODE = stringPreferencesKey("recent_layout_mode")
        val KEY_SHELTER_LAYOUT_MODE = stringPreferencesKey("shelter_layout_mode")
        val KEY_FAVORITE_LAYOUT_MODE = stringPreferencesKey("favorite_layout_mode")
        val KEY_HOME_SIDO_CODE = stringPreferencesKey("home_sido_code")
        val KEY_HOME_SIDO_NAME = stringPreferencesKey("home_sido_name")
        val KEY_HOME_SIGUNGU_CODE = stringPreferencesKey("home_sigungu_code")
        val KEY_HOME_SIGUNGU_NAME = stringPreferencesKey("home_sigungu_name")
        val KEY_HOME_SIGUNGU_PARENT_CODE = stringPreferencesKey("home_sigungu_parent_code")
        val KEY_HOME_UP_KIND = stringPreferencesKey("home_up_kind")
        val KEY_HOME_DATE_RANGE = stringPreferencesKey("home_date_range")
    }
}
