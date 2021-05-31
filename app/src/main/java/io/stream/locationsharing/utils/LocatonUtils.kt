package io.stream.locationsharing.utils

import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

@OptIn(ExperimentalCoroutinesApi::class)
fun FusedLocationProviderClient.locationFlow() = callbackFlow<Location> {
    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result ?: return
            try { trySend(result.lastLocation) } catch(e: Exception) {
                Log.e("Error", e.message.toString())
            }
        }
    }
    requestLocationUpdates(createLocationRequest(), callback, Looper.getMainLooper())
        .addOnFailureListener { e ->
            close(e)
        }

    awaitClose {
        removeLocationUpdates(callback)
    }
}

fun createLocationRequest(): LocationRequest {
    return LocationRequest.create().apply {
        interval = 20000
        fastestInterval = 10000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
}
