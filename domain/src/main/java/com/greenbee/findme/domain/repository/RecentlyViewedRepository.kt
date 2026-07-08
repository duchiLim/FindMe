package com.greenbee.findme.domain.repository

import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.model.RecentlyViewedAnimal
import kotlinx.coroutines.flow.Flow

interface RecentlyViewedRepository {
    fun observeRecentlyViewed(limit: Int = DEFAULT_PREVIEW_LIMIT): Flow<List<RecentlyViewedAnimal>>

    suspend fun recordView(animal: AbandonedAnimal)

    companion object {
        const val DEFAULT_PREVIEW_LIMIT = 10
        const val MAX_STORED_COUNT = 30
    }
}
