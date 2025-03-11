import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
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
import com.example.room_hilt.ui.home.HomeViewModel
import com.example.room_hilt.ui.home.LocationUtils
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.collectAsState
import com.example.room_hilt.data.MyItem
// for a 'val' variable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.room_hilt.ui.home.ItemUiState

@Composable
fun LocationScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var locationPermissionsAlreadyGranted = remember { mutableStateOf(false) }

    locationPermissionsAlreadyGranted.value = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
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
    val locationState = remember { mutableStateOf<Location?>(null) }
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
                }
                ) {
                    Text("Grant Permission")
                }
            }

            !locationEnabledState.value -> {
                Text("GPS is off. Please enable GPS.")
            }

            else -> {
                Text("GPS is ON and Permission is granted")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    LocationUtils.requestCurrentLocation(context) { location ->
                        locationState.value = location
                        location?.let {
                            val myItem = MyItem(
                                latitude = it.latitude, longitude = it.longitude,
                                timestamp = System.currentTimeMillis()
                            )
                            viewModel.addItem(myItem)
                        }
                    }
                }) {
                    Text("Start")
                }
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
    Column {
        locations.forEach { location ->
            Text(text = location.latitude.toString() + ", " + location.longitude.toString()) // Replace with your UI representation
        }
    }
}

fun launchPermissionRequest() {

}