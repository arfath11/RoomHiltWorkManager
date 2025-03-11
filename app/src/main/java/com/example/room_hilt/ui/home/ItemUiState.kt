package com.example.room_hilt.ui.home

import com.example.room_hilt.data.MyItem

sealed class ItemUiState {
    data class Success(val myItems: List<MyItem>) : ItemUiState()
    data object Loading : ItemUiState()
    data class Error(val message: String) : ItemUiState()
}
