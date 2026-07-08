package com.greenbee.findme.data.location

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.greenbee.findme.domain.model.GeoLocation
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class GeocodingDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val cache = mutableMapOf<String, GeoLocation?>()

    suspend fun geocodeAddress(address: String): GeoLocation? = withContext(Dispatchers.IO) {
        val normalized = address.trim()
        if (normalized.isBlank() || normalized == "주소 정보 없음") return@withContext null
        cache[normalized]?.let { return@withContext it }

        if (!Geocoder.isPresent()) {
            cache[normalized] = null
            return@withContext null
        }

        val geocoder = Geocoder(context, Locale.KOREA)
        val location = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCoroutine { continuation ->
                    geocoder.getFromLocationName(normalized, 1) { addresses ->
                        continuation.resume(
                            addresses.firstOrNull()?.let { address ->
                                GeoLocation(
                                    latitude = address.latitude,
                                    longitude = address.longitude,
                                )
                            },
                        )
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocationName(normalized, 1)
                    ?.firstOrNull()
                    ?.let { address ->
                        GeoLocation(
                            latitude = address.latitude,
                            longitude = address.longitude,
                        )
                    }
            }
        }.getOrElse { throwable ->
            if (throwable is IOException) null else throw throwable
        }

        cache[normalized] = location
        location
    }
}
