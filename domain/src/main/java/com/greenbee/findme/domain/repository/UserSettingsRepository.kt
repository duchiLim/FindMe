package com.greenbee.findme.domain.repository

import com.greenbee.findme.domain.model.HomeDateRange
import com.greenbee.findme.domain.model.HomeLayoutMode
import com.greenbee.findme.domain.model.HomeRegionFilter
import com.greenbee.findme.domain.model.UpKind
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    fun isNotificationEnabled(): Boolean

    fun setNotificationEnabled(enabled: Boolean)

    fun isRecentViewingEnabled(): Boolean

    fun setRecentViewingEnabled(enabled: Boolean)

    fun observeHomeLayoutMode(): Flow<HomeLayoutMode>

    suspend fun setHomeLayoutMode(mode: HomeLayoutMode)

    fun observeRecentLayoutMode(): Flow<HomeLayoutMode>

    suspend fun setRecentLayoutMode(mode: HomeLayoutMode)

    fun observeShelterLayoutMode(): Flow<HomeLayoutMode>

    suspend fun setShelterLayoutMode(mode: HomeLayoutMode)

    fun observeFavoriteLayoutMode(): Flow<HomeLayoutMode>

    suspend fun setFavoriteLayoutMode(mode: HomeLayoutMode)

    suspend fun getHomeRegionFilter(): HomeRegionFilter

    suspend fun setHomeRegionFilter(filter: HomeRegionFilter)

    suspend fun getHomeUpKindFilter(): UpKind?

    suspend fun setHomeUpKindFilter(upKind: UpKind?)

    suspend fun getHomeDateRangeFilter(): HomeDateRange

    suspend fun setHomeDateRangeFilter(dateRange: HomeDateRange)
}
