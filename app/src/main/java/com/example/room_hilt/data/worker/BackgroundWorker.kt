package com.example.room_hilt.data.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.hilt.work.HiltWorker
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.room_hilt.data.ItemDao
import com.example.room_hilt.data.MyDatabase
import com.example.room_hilt.data.MyItem
import com.example.room_hilt.ui.home.LocationUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext


@HiltWorker
class BackgroundWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
   //   private val myDao: ItemDao, // not able to add it through DI
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        return try {
            fetchAndStoreLocation()
            Log.e("BackgroundWorker", "Location fetch successfull in backgroiund")
            Result.success() // Mark as successful
        } catch (e: Exception) {
            Log.e("BackgroundWorker", "Location fetch failed", e)
            Result.failure()
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
      val myDao = myItemDao( myDatabase(context))

      item?.let {
          myDao.insertItem(it)
          Log.e("BackgroundWorker", "Item added successfully $it ")
      } ?: Log.e("BackgroundWorker", "Failed to get location")

  }


    fun myDatabase(@ApplicationContext context: Context): MyDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            MyDatabase::class.java,
            "my_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    fun myItemDao(database: MyDatabase): ItemDao {
        return database.itemDao()
    }


}
