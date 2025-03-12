package com.example.room_hilt.ui.home


import android.Manifest
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

object LocationUtils {
    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION]
    )
    suspend fun requestCurrentLocation(context: Context): Location? {
        return suspendCancellableCoroutine { continuation ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    continuation.resume(location) {}
                }
                .addOnFailureListener { e ->
                    Log.e("LocationUtils", "Failed to fetch location", e)
                    continuation.resume(null) {}
                }
        }
    }
}