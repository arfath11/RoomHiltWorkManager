package com.example.room_hilt.data

import com.example.room_hilt.domain.usecase.Result
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getItems(): Flow<Result<List<MyItem>>> // Automatically updates the UI when data changes
    suspend fun addItem(item: MyItem): Result<Unit>
    suspend fun deleteAllItem(): Result<Unit>
}