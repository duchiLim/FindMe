package com.greenbee.findme.domain.usecase

import com.greenbee.findme.domain.model.GeoLocation
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.domain.model.Shelter
import com.greenbee.findme.domain.model.distanceMetersTo
import com.greenbee.findme.domain.repository.GeocodingRepository
import com.greenbee.findme.domain.repository.LocationRepository
import javax.inject.Inject

class GeocodeAddressUseCase @Inject constructor(
    private val geocodingRepository: GeocodingRepository,
) {
    suspend operator fun invoke(address: String): GeoLocation? =
        geocodingRepository.geocodeAddress(address)
}

class GetCurrentLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
) {
    suspend operator fun invoke(): GeoLocation? =
        locationRepository.getCurrentLocation()
}

class ResolveRegionFromLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
) {
    suspend operator fun invoke(
        location: GeoLocation,
        sidoList: List<Region>,
        sigunguProvider: suspend (String) -> List<Region>,
    ): Pair<Region, Region>? = locationRepository.resolveRegion(
        location = location,
        sidoList = sidoList,
        sigunguProvider = sigunguProvider,
    )
}

data class ShelterWithDistance(
    val shelter: Shelter,
    val distanceMeters: Int?,
    val location: GeoLocation?,
)

class SortSheltersByDistanceUseCase @Inject constructor(
    private val geocodeAddressUseCase: GeocodeAddressUseCase,
) {
    suspend operator fun invoke(
        shelters: List<Shelter>,
        userLocation: GeoLocation,
    ): List<ShelterWithDistance> {
        return shelters
            .map { shelter ->
                val location = geocodeAddressUseCase(shelter.address)
                val distanceMeters = location?.let { userLocation.distanceMetersTo(it).toInt() }
                ShelterWithDistance(
                    shelter = shelter,
                    distanceMeters = distanceMeters,
                    location = location,
                )
            }
            .sortedBy { it.distanceMeters ?: Int.MAX_VALUE }
    }
}
