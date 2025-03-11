package com.example.room_hilt.data

import androidx.room.Database
import androidx.room.RoomDatabase
import javax.inject.Singleton
import dagger.Module
import dagger.Provides
import android.content.Context
import androidx.room.Room
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Database(entities = [MyItem::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MyDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            MyDatabase::class.java,
            "my_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideItemDao(database: MyDatabase): ItemDao {
        return database.itemDao()
    }
}
