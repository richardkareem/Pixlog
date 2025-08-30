package com.richard.pixlog.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.richard.pixlog.data.local.datastore.LoginPreferences
import com.richard.pixlog.data.remote.request.LoginRequest
import com.richard.pixlog.data.remote.request.RegisterRequest
import com.richard.pixlog.data.remote.request.StoryRequest
import com.richard.pixlog.data.remote.response.ListStory
import com.richard.pixlog.data.remote.response.LoginResponse
import com.richard.pixlog.data.remote.response.RegisterResponse
import com.richard.pixlog.data.remote.response.StoryResponse
import com.richard.pixlog.data.remote.response.UploadResponse
import com.richard.pixlog.data.remote.retrofit.ApiService
import com.richard.pixlog.ui.screen.home.HomePagingSource
import com.richard.pixlog.utils.AppExecutors
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class PixlogRepository private constructor(
    private val apiService: ApiService,
    private val appExecutors: AppExecutors,
    private val dataStore: LoginPreferences
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

    suspend fun getAllResponse(bodyRequest: StoryRequest) : StoryResponse {
     return apiService.getAllStory()
    }

    fun getAllStory() : LiveData<PagingData<ListStory>>{
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                HomePagingSource(apiService)
            }
        ).liveData
    }


    suspend fun uploadStory(file: MultipartBody.Part, requestBody: RequestBody): UploadResponse{
        return  apiService.addStory(file, requestBody)
    }

    companion object{

        @Volatile
        private var instance : PixlogRepository? = null
        fun getInstance(
            apiService: ApiService,
            appExecutors: AppExecutors,
            dataStore: LoginPreferences
        ): PixlogRepository =
            instance?: synchronized(this){
                instance?: PixlogRepository(apiService, appExecutors, dataStore)
            }.also { instance = it }

    }
}