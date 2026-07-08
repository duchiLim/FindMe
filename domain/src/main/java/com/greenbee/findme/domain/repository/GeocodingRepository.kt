package com.greenbee.findme.domain.repository

import com.greenbee.findme.domain.model.GeoLocation

interface GeocodingRepository {
    suspend fun geocodeAddress(address: String): GeoLocation?
}
