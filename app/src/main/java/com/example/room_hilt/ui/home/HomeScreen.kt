package com.example.room_hilt.ui.home

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import android.Manifest
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.collectAsState
import com.example.room_hilt.data.MyItem
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.room_hilt.data.worker.BackgroundWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun LocationScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var locationPermissionsAlreadyGranted = remember { mutableStateOf(false) }

    locationPermissionsAlreadyGranted.value = ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                val permissionsGranted = permissions.values.reduce { acc, isPermissionGranted ->
                    acc && isPermissionGranted
                }
                locationPermissionsAlreadyGranted.value = permissionsGranted

                if (!permissionsGranted) {
                    //Logic when the permissions were not granted by the user
                }
            })

    val locationEnabledState = remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()


    // Check if GPS is enabled
    LaunchedEffect(Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationEnabledState.value = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            !locationPermissionsAlreadyGranted.value -> {
                Text("Location permission required")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    locationPermissionLauncher.launch(locationPermissions)
                }) {
                    Text("Grant Permission")
                }
            }

            !locationEnabledState.value -> {
                Text("GPS is off. Please enable GPS.")
            }

            else -> {
              MyButtons()
            }
        }

        when (uiState) {
            is ItemUiState.Loading -> {
                //todo
            }

            is ItemUiState.Success -> {
                ItemList((uiState as ItemUiState.Success).myItems)
            }

            is ItemUiState.Error -> {
                val message = (uiState as ItemUiState.Error).message
                Text("Error: $message", color = Color.Red)
            }

        }
    }
}


@Composable
fun ItemList(locations: List<MyItem>) {
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    var previousLocation: MyItem? = null

    Column(modifier = Modifier.padding(16.dp)) {
        locations.forEach { currentLocation ->
            val formattedTime = timeFormatter.format(Date(currentLocation.timestamp))

            val distanceText = if (previousLocation != null) {
                val distanceInMeters = FloatArray(1)
                Location.distanceBetween(
                    previousLocation!!.latitude, previousLocation!!.longitude,
                    currentLocation.latitude, currentLocation.longitude,
                    distanceInMeters
                )
                formatDistance(distanceInMeters[0])
            } else {
                "First recorded location"
            }

            Text(
                text = "${currentLocation.latitude}, ${currentLocation.longitude} -- $formattedTime",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = distanceText,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            previousLocation = currentLocation
        }
    }
}

fun formatDistance(distanceInMeters: Float): String {
    return if (distanceInMeters < 1000) {
        "Distance: %.2f meters".format(distanceInMeters)
    } else {
        val distanceInKilometers = distanceInMeters / 1000
        "Distance: %.2f km".format(distanceInKilometers)
    }
}



@Composable
fun MyButtons(viewModel: HomeViewModel = hiltViewModel(), context: Context = LocalContext.current) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("GPS is ON and Permission is granted")
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Button(onClick = {
              //  scheduleBackgroundWorker(context)
               viewModel.scheduleBackgroundWorker()
            }) {
                Text("Start")
            }

            Button(
                onClick = { viewModel.deleteAllItem() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Delete All", color = Color.White)
            }
        }
    }
}
