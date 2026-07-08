package com.greenbee.findme.presentation.shelter.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.greenbee.findme.domain.model.GeoLocation

fun openMap(context: Context, address: String, location: GeoLocation?) {
    val uri = if (location != null) {
        Uri.parse("geo:${location.latitude},${location.longitude}?q=${Uri.encode(address)}")
    } else {
        Uri.parse("geo:0,0?q=${Uri.encode(address)}")
    }
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(Intent.createChooser(intent, null))
}

fun openDirections(context: Context, address: String, location: GeoLocation?) {
    val uri = if (location != null) {
        Uri.parse("google.navigation:q=${location.latitude},${location.longitude}")
    } else {
        Uri.parse("google.navigation:q=${Uri.encode(address)}")
    }
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(intent) }
        .onFailure { openMap(context, address, location) }
}

fun buildStaticMapUrl(location: GeoLocation, width: Int = 640, height: Int = 280): String {
    val lat = location.latitude
    val lng = location.longitude
    return "https://staticmap.openstreetmap.de/staticmap.php" +
        "?center=$lat,$lng&zoom=15&size=${width}x$height&markers=$lat,$lng"
}
