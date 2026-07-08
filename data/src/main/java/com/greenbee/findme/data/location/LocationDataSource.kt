package com.greenbee.findme.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import com.greenbee.findme.domain.model.GeoLocation
import com.greenbee.findme.domain.model.Region
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.util.Locale
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class LocationDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): GeoLocation? = withContext(Dispatchers.IO) {
        val locationManager = context.getSystemService(LocationManager::class.java) ?: return@withContext null
        val provider = when {
            locationManager.isProviderEnabled(LocationManager.FUSED_PROVIDER) ->
                LocationManager.FUSED_PROVIDER
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                LocationManager.NETWORK_PROVIDER
            else -> return@withContext null
        }

        suspendCoroutine { continuation ->
            val executor = Executors.newSingleThreadExecutor()
            locationManager.getCurrentLocation(
                provider,
                CancellationSignal(),
                executor,
            ) { location ->
                executor.shutdown()
                if (location == null) {
                    continuation.resume(null)
                } else {
                    continuation.resume(
                        GeoLocation(
                            latitude = location.latitude,
                            longitude = location.longitude,
                        ),
                    )
                }
            }
        }
    }

    suspend fun resolveRegion(
        location: GeoLocation,
        sidoList: List<Region>,
        sigunguProvider: suspend (String) -> List<Region>,
    ): Pair<Region, Region>? = withContext(Dispatchers.IO) {
        if (!Geocoder.isPresent()) return@withContext null

        val geocoder = Geocoder(context, Locale.KOREA)
        val address = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCoroutine { continuation ->
                    geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                        continuation.resume(addresses.firstOrNull())
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()
            }
        }.getOrElse { throwable ->
            if (throwable is IOException) null else throw throwable
        } ?: return@withContext null

        val adminArea = address.adminArea?.trim().orEmpty()
        val subAdminArea = address.subAdminArea?.trim().orEmpty()
        val locality = address.locality?.trim().orEmpty()
        val sigunguCandidate = subAdminArea.ifBlank { locality }
        if (adminArea.isBlank() || sigunguCandidate.isBlank()) return@withContext null

        val sido = sidoList.find { region ->
            region.name.equals(adminArea, ignoreCase = true) ||
                adminArea.contains(region.name, ignoreCase = true) ||
                region.name.contains(adminArea, ignoreCase = true)
        } ?: return@withContext null

        val sigunguList = sigunguProvider(sido.code)
        val sigungu = sigunguList.find { region ->
            region.name.equals(sigunguCandidate, ignoreCase = true) ||
                sigunguCandidate.contains(region.name, ignoreCase = true) ||
                region.name.contains(sigunguCandidate, ignoreCase = true)
        } ?: return@withContext null

        sido to sigungu
    }
}
