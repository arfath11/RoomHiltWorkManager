package com.example.room_hilt.data


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

import com.example.room_hilt.domain.usecase.Result

@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao
) : ItemRepository {

    override fun getItems(): Flow<Result<List<MyItem>>> = flow {
        emit(Result.Loading)
        try {
            itemDao.getAllItems().collect { items ->
                emit(Result.Success(items))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun addItem(item: MyItem): Result<Unit> {
        return try {
            itemDao.insertItem(item)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteAllItem(): Result<Unit> {
        return try {
            itemDao.deleteAllItems()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }    }


}
