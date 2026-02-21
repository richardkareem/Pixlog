package com.richard.pixlog.data.local.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.richard.pixlog.data.local.entity.ListStoryEntity

@Dao
interface ListStoryDAO {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertStory(story: List<ListStoryEntity>)
    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, ListStoryEntity>
    @Query("DELETE FROM story")
    suspend fun deleteAllStory()
    @Query("SELECT * FROM story WHERE lat IS NOT NULL AND lon IS NOT NULL")
    fun getAllStoryWithLocation(): List<ListStoryEntity>
}