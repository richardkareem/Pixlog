package com.richard.pixlog.data.remote.retrofit

import android.util.Log
import com.richard.pixlog.BuildConfig
import com.richard.pixlog.BuildConfig.BASE_URL
import com.richard.pixlog.data.local.datastore.LoginPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        private const val API_CONFIG = BASE_URL

        // Build ApiService with an interceptor that fetches the latest token from DataStore per request
        fun getApiServuce(loginPreferences: LoginPreferences): ApiService {
            val authInterceptor = Interceptor { chain ->
                val token = runBlocking { loginPreferences.getToken().first() }
                val original = chain.request()
                val request = if (token.isNullOrEmpty()) {
                    original
                } else {
                    // add new request with auth header
                    original.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                }
                chain.proceed(request)
            }

            val loggingInterceptor = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(API_CONFIG)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}