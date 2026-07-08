package com.greenbee.findme.presentation.shelter.model

import com.greenbee.findme.domain.model.FavoriteShelter
import com.greenbee.findme.domain.model.GeoLocation
import com.greenbee.findme.domain.model.Shelter

data class ShelterUiModel(
    val careRegNo: String,
    val name: String,
    val orgName: String,
    val tel: String,
    val address: String,
    val canCall: Boolean,
    val distanceMeters: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isFavorite: Boolean = false,
) {
    val mapLocation: GeoLocation?
        get() = if (latitude != null && longitude != null) {
            GeoLocation(latitude = latitude, longitude = longitude)
        } else {
            null
        }
}

fun Shelter.toUiModel(
    distanceMeters: Int? = null,
    location: GeoLocation? = null,
    isFavorite: Boolean = false,
): ShelterUiModel = ShelterUiModel(
    careRegNo = careRegNo,
    name = name.ifBlank { "보호소 정보 없음" },
    orgName = orgName,
    tel = tel,
    address = address.ifBlank { "주소 정보 없음" },
    canCall = tel.isNotBlank(),
    distanceMeters = distanceMeters,
    latitude = location?.latitude,
    longitude = location?.longitude,
    isFavorite = isFavorite,
)

fun FavoriteShelter.toUiModel(isFavorite: Boolean = true): ShelterUiModel = ShelterUiModel(
    careRegNo = careRegNo,
    name = name.ifBlank { "보호소 정보 없음" },
    orgName = orgName,
    tel = tel,
    address = address.ifBlank { "주소 정보 없음" },
    canCall = tel.isNotBlank(),
    isFavorite = isFavorite,
)

fun formatDistance(meters: Int?): String? {
    if (meters == null) return null
    return if (meters >= 1000) {
        String.format("%.1fkm", meters / 1000.0)
    } else {
        "${meters}m"
    }
}
