package com.example.room_hilt.domain.usecase

import com.example.room_hilt.data.ItemRepository
import com.example.room_hilt.data.MyItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetItemsUseCase @Inject constructor(private val repository: ItemRepository) {
    operator fun invoke(): Flow<Result<List<MyItem>>> = repository.getItems()
}