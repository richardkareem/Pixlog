package com.richard.pixlog.data.remote.retrofit

import com.richard.pixlog.data.remote.request.LoginRequest
import com.richard.pixlog.data.remote.request.RegisterRequest
import com.richard.pixlog.data.remote.request.StoryRequest
import com.richard.pixlog.data.remote.response.LoginResponse
import com.richard.pixlog.data.remote.response.RegisterResponse
import com.richard.pixlog.data.remote.response.StoryResponse
import com.richard.pixlog.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    @Headers("Content-Type: application/json")
    suspend  fun register(
        @Body requestBody: RegisterRequest
    ): RegisterResponse

    @POST("login")
    @Headers("Content-Type: application/json")
    suspend fun login(
        @Body requestBody: LoginRequest
    ): LoginResponse

    @GET("stories")
    @Headers("Content-Type: application/json")
    suspend fun getAllStory(
        @Query("page") page : Int ? = 1,
        @Query("size") size : Int? = 10,
        @Query("location") location : Int ? = 0
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): UploadResponse
}