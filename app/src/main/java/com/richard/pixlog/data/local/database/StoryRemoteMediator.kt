package com.richard.pixlog.data.local.database

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.richard.pixlog.data.local.entity.ListStoryEntity
import com.richard.pixlog.data.local.entity.RemoteKeys
import com.richard.pixlog.data.remote.response.ListStory
import com.richard.pixlog.data.remote.retrofit.ApiService

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: PixlogDatabase,
    private val apiService: ApiService
): RemoteMediator<Int, ListStoryEntity>(){
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
    
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
    
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    prevKey
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                    
                    // If no next key, continue to next page
                    if (nextKey == null) {
                        INITIAL_PAGE_INDEX + 1
                    } else {
                        nextKey
                    }
                }
            }
            
            val response = apiService.getAllStory(page, state.config.pageSize)
            val responseData = response.listStory

            // End of pagination reached when response data is less than page size
            val endOfPaginationReached = responseData.size < state.config.pageSize
            
            android.util.Log.d("StoryRemoteMediator", "endOfPaginationReached: $endOfPaginationReached")
            
            // Convert ListStory to ListStoryEntity
            val storyEntities = responseData.map { story ->
                ListStoryEntity(
                    id = story.id,
                    name = story.name,
                    description = story.description,
                    photoUrl = story.photoUrl,
                    createdAt = story.createdAt,
                    lat = story.lat,
                    lon = story.lon
                )
            }
            
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.listStoryDAO().deleteAllStory()
                }
                
                // Insert stories first
                database.listStoryDAO().insertStory(storyEntities)
                
                // Then insert remote keys
                if (responseData.isNotEmpty()) {
                    val prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = responseData.map { story ->
                        RemoteKeys(id = story.id, prevKey = prevKey, nextKey = nextKey)
                    }
                    database.remoteKeysDao().insertAll(keys)
                }
            }

            android.util.Log.d("StoryRemoteMediator", "Returning MediatorResult.Success with endOfPaginationReached: $endOfPaginationReached")
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }
    
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }
    
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStoryEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }
    
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStoryEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }
}