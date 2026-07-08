package com.greenbee.findme.data.repository

import com.greenbee.findme.data.location.GeocodingDataSource
import com.greenbee.findme.domain.model.GeoLocation
import com.greenbee.findme.domain.repository.GeocodingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeocodingRepositoryImpl @Inject constructor(
    private val geocodingDataSource: GeocodingDataSource,
) : GeocodingRepository {

    override suspend fun geocodeAddress(address: String): GeoLocation? =
        geocodingDataSource.geocodeAddress(address)
}
