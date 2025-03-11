package com.example.room_hilt.domain.usecase

import com.example.room_hilt.data.ItemRepository
import com.example.room_hilt.data.MyItem
import javax.inject.Inject

class AddItemUseCase @Inject constructor(private  val repository: ItemRepository){

    suspend operator fun invoke(item: MyItem): Result<Unit> {
        return repository.addItem(item)
    }

}