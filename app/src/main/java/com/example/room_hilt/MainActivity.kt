package com.example.room_hilt

import com.example.room_hilt.ui.home.LocationScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.room_hilt.ui.theme.Room_hiltTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Room_hiltTheme {
                LocationScreen()
            }
        }
    }
}

