package com.richard.pixlog.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.richard.pixlog.data.local.entity.ListStoryEntity
import com.richard.pixlog.data.local.entity.RemoteKeys

@Database(
    entities = [ListStoryEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class PixlogDatabase : RoomDatabase() {
    abstract fun listStoryDAO(): ListStoryDAO
    abstract fun remoteKeysDao(): RemoteKeysDAO
    companion object {
        @Volatile
        private var INSTANCE : PixlogDatabase? = null
        @JvmStatic
        //factory method
        fun getDatabase(context: Context): PixlogDatabase{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PixlogDatabase::class.java,
                    "pixlog_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}