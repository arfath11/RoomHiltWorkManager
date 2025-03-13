package com.example.room_hilt.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM MyTable ORDER BY timestamp ASC")
    fun getAllItems(): Flow<List<MyItem>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(myItem: MyItem): Long

    @Query("DELETE FROM MyTable")
    suspend fun deleteAllItems()
}
