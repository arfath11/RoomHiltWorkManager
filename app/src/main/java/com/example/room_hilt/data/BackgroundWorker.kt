package com.example.room_hilt.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.room_hilt.domain.usecase.AddItemUseCase
import com.example.room_hilt.ui.home.LocationUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
@HiltWorker
class BackgroundWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    @Assisted private val addItemUseCase: AddItemUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            fetchAndStoreLocation()
            Log.e("BackgroundWorker", "Location fetch successfulll in backgroiund")

            Result.success() // Mark as successful
        } catch (e: Exception) {
            Log.e("BackgroundWorker", "Location fetch failed", e)
            Result.failure() // Mark as failed
        }
    }

  suspend    fun fetchAndStoreLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

            return
        }
      val location = LocationUtils.requestCurrentLocation(context)
      var item: MyItem? = location?.let {
          MyItem(
              latitude = it.latitude,
              longitude = it.longitude,
              timestamp = System.currentTimeMillis()
          )
      }

      item?.let {
          addItemUseCase(it)
          Log.e("BackgroundWorker", "Item added successfully")
      } ?: Log.e("BackgroundWorker", "Failed to get location")

  }}
