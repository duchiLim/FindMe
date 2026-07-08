package com.greenbee.findme.domain.repository

import com.greenbee.findme.domain.model.GeoLocation
import com.greenbee.findme.domain.model.Region

interface LocationRepository {
    suspend fun getCurrentLocation(): GeoLocation?

    suspend fun resolveRegion(
        location: GeoLocation,
        sidoList: List<Region>,
        sigunguProvider: suspend (String) -> List<Region>,
    ): Pair<Region, Region>?
}
