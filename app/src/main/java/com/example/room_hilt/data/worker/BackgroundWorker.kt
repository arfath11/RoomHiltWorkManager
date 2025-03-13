package com.example.room_hilt.data.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.room_hilt.data.ItemDao
import com.example.room_hilt.data.MyItem
import com.example.room_hilt.ui.home.LocationUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class BackgroundWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val itemDao: ItemDao // Hilt will inject this properly with @AssistedInject
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            fetchAndStoreLocation()
            Log.e("BackgroundWorker", "Location fetch successful in background")
            Result.success() // Mark as successful
        } catch (e: Exception) {
            Log.e("BackgroundWorker", "Location fetch failed", e)
            Result.failure()
        }
    }

    suspend fun fetchAndStoreLocation() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        
        val location = LocationUtils.requestCurrentLocation(applicationContext)
        val item: MyItem? = location?.let {
            MyItem(
                latitude = it.latitude,
                longitude = it.longitude,
                timestamp = System.currentTimeMillis()
            )
        }

        // Use the injected DAO directly
        item?.let {
            itemDao.insertItem(it)
            Log.e("BackgroundWorker", "Item added successfully $it ")
        } ?: Log.e("BackgroundWorker", "Failed to get location")
    }
}
