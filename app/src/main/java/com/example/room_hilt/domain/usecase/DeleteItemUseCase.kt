package com.example.room_hilt.domain.usecase

import com.example.room_hilt.data.ItemRepository
import com.example.room_hilt.domain.model.Result
import javax.inject.Inject

class DeleteItemUseCase @Inject constructor(private val repository: ItemRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.deleteAllItem()
    }

}