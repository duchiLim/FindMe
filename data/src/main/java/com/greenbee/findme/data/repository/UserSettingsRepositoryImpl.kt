package com.greenbee.findme.data.repository

import com.greenbee.findme.data.settings.UserPreferencesDataStore
import com.greenbee.findme.data.settings.UserSettingsDataSource
import com.greenbee.findme.domain.model.HomeDateRange
import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.domain.model.HomeRegionFilter
import com.greenbee.findme.domain.model.UpKind
import com.greenbee.findme.domain.repository.UserSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class UserSettingsRepositoryImpl @Inject constructor(
    private val dataSource: UserSettingsDataSource,
    private val preferencesDataStore: UserPreferencesDataStore,
) : UserSettingsRepository {

    override fun isNotificationEnabled(): Boolean = dataSource.isNotificationEnabled()

    override fun setNotificationEnabled(enabled: Boolean) {
        dataSource.setNotificationEnabled(enabled)
    }

    override fun isRecentViewingEnabled(): Boolean = dataSource.isRecentViewingEnabled()

    override fun setRecentViewingEnabled(enabled: Boolean) {
        dataSource.setRecentViewingEnabled(enabled)
    }

    override fun observeHomeLayoutMode(): Flow<HomeLayoutMode> =
        preferencesDataStore.homeLayoutMode

    override suspend fun setHomeLayoutMode(mode: HomeLayoutMode) {
        preferencesDataStore.setHomeLayoutMode(mode)
    }

    override fun observeRecentLayoutMode(): Flow<HomeLayoutMode> =
        preferencesDataStore.recentLayoutMode

    override suspend fun setRecentLayoutMode(mode: HomeLayoutMode) {
        preferencesDataStore.setRecentLayoutMode(mode)
    }

    override fun observeShelterLayoutMode(): Flow<HomeLayoutMode> =
        preferencesDataStore.shelterLayoutMode

    override suspend fun setShelterLayoutMode(mode: HomeLayoutMode) {
        preferencesDataStore.setShelterLayoutMode(mode)
    }

    override fun observeFavoriteLayoutMode(): Flow<HomeLayoutMode> =
        preferencesDataStore.favoriteLayoutMode

    override suspend fun setFavoriteLayoutMode(mode: HomeLayoutMode) {
        preferencesDataStore.setFavoriteLayoutMode(mode)
    }

    override suspend fun getHomeRegionFilter(): HomeRegionFilter =
        preferencesDataStore.getHomeRegionFilter()

    override suspend fun setHomeRegionFilter(filter: HomeRegionFilter) {
        preferencesDataStore.setHomeRegionFilter(filter)
    }

    override suspend fun getHomeUpKindFilter(): UpKind? =
        preferencesDataStore.getHomeUpKindFilter()

    override suspend fun setHomeUpKindFilter(upKind: UpKind?) {
        preferencesDataStore.setHomeUpKindFilter(upKind)
    }

    override suspend fun getHomeDateRangeFilter(): HomeDateRange =
        preferencesDataStore.getHomeDateRangeFilter()

    override suspend fun setHomeDateRangeFilter(dateRange: HomeDateRange) {
        preferencesDataStore.setHomeDateRangeFilter(dateRange)
    }
}
