package com.example.room_hilt.data

import com.example.room_hilt.domain.model.Result
import kotlinx.coroutines.flow.Flow


import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class  ItemRepository @Inject constructor(
    private val itemDao: ItemDao
)  {

     fun getItems(): Flow<Result<List<MyItem>>> = flow {
        emit(Result.Loading)
        try {
            itemDao.getAllItems().collect { items ->
                emit(Result.Success(items))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

     suspend fun addItem(item: MyItem): Result<Unit> {
        return try {
            itemDao.insertItem(item)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

     suspend fun deleteAllItem(): Result<Unit> {
        return try {
            itemDao.deleteAllItems()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }    }


}
