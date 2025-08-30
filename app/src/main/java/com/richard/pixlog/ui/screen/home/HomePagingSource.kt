package com.richard.pixlog.ui.screen.home

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.richard.pixlog.data.remote.response.ListStory
import com.richard.pixlog.data.remote.retrofit.ApiService

class HomePagingSource(private val apiService: ApiService) : PagingSource<Int, ListStory>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
    
    override fun getRefreshKey(state: PagingState<Int, ListStory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStory> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            
            val response = apiService.getAllStory(
                page = position,
                size = params.loadSize,
                location = 0
            )
            
            val data = response.listStory
            
            LoadResult.Page(
                data = data,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (data.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            Log.e("HomePagingSource", "Error loading data: ${e.message}", e)
            return LoadResult.Error(e)
        }
    }
}
