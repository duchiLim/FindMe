package com.greenbee.findme.data.repository

import com.greenbee.findme.data.location.LocationDataSource
import com.greenbee.findme.domain.model.GeoLocation
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.domain.repository.LocationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val locationDataSource: LocationDataSource,
) : LocationRepository {

    override suspend fun getCurrentLocation(): GeoLocation? =
        locationDataSource.getCurrentLocation()

    override suspend fun resolveRegion(
        location: GeoLocation,
        sidoList: List<Region>,
        sigunguProvider: suspend (String) -> List<Region>,
    ): Pair<Region, Region>? = locationDataSource.resolveRegion(
        location = location,
        sidoList = sidoList,
        sigunguProvider = sigunguProvider,
    )
}
