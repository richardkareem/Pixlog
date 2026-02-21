package com.richard.pixlog.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import androidx.room.withTransaction
import com.richard.pixlog.data.local.database.PixlogDatabase
import com.richard.pixlog.data.local.database.StoryRemoteMediator
import com.richard.pixlog.data.local.datastore.LoginPreferences
import com.richard.pixlog.data.local.entity.ListStoryEntity
import com.richard.pixlog.data.remote.request.LoginRequest
import com.richard.pixlog.data.remote.request.RegisterRequest
import com.richard.pixlog.data.remote.request.StoryRequest
import com.richard.pixlog.data.remote.response.ListStory
import com.richard.pixlog.data.remote.response.LoginResponse
import com.richard.pixlog.data.remote.response.RegisterResponse
import com.richard.pixlog.data.remote.response.StoryResponse
import com.richard.pixlog.data.remote.response.UploadResponse
import com.richard.pixlog.data.remote.retrofit.ApiService
import com.richard.pixlog.data.adapters.StoryPagingSource
import com.richard.pixlog.utils.AppExecutors
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class PixlogRepository private constructor(
    private val apiService: ApiService,
    private val appExecutors: AppExecutors,
    private val dataStore: LoginPreferences,
    private val database : PixlogDatabase,
){
    //suspend dari retrofit handle async menjadi seperti syncronous
     suspend fun postRegister(name: String, email: String, password:String) : RegisterResponse{
        val body = RegisterRequest(name, email, password)
        val response = apiService.register(body)
        return  response
    }

    suspend fun postLogin(email: String, password: String): LoginResponse{
        val body = LoginRequest(email, password)
        val result = apiService.login(body)

        dataStore.saveDataLogin(result)
        return result
    }

    fun getAllStoryWithNetwork() : LiveData<PagingData<ListStoryEntity>> {
      return Pager(
          config = PagingConfig(
              pageSize = 5
          ),
          pagingSourceFactory = {
              StoryPagingSource(apiService)
          }
      ).liveData
    }

    suspend fun getAllStoryWithLocation() : StoryResponse{
        return apiService.getAllStory(
            location = 1,
            size = 10000
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStory() : LiveData<PagingData<ListStoryEntity>>{
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            remoteMediator = StoryRemoteMediator(database, apiService),
            pagingSourceFactory = {
                database.listStoryDAO().getAllStory()
            }
        ).liveData
    }

    suspend fun getAllLocationStory(): List<ListStoryEntity> {
        return database.withTransaction {
            database.listStoryDAO().getAllStoryWithLocation()
        }
    }


    suspend fun uploadStory(file: MultipartBody.Part, requestBody: RequestBody, lat: Double? = null, lon: Double? = null): UploadResponse{
        val latRequestBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
        val lonRequestBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())
        return  apiService.addStory(file, requestBody, latRequestBody, lonRequestBody)
    }

    suspend fun logout(){
        dataStore.clearToken()
        database.listStoryDAO().deleteAllStory()
        database.remoteKeysDao().deleteRemoteKeys()
    }

     fun getNamePreferences() : Flow<String>{
        return dataStore.getName()
    }


    //singleton object
    // factory method
    companion object{

        @Volatile
        private var instance : PixlogRepository? = null
        fun getInstance(
            apiService: ApiService,
            appExecutors: AppExecutors,
            dataStore: LoginPreferences,
            database: PixlogDatabase
        ): PixlogRepository =
            instance?: synchronized(this){
                instance?: PixlogRepository(apiService, appExecutors, dataStore, database)
            }.also { instance = it }

    }
}