package com.richard.pixlog.data.di

import android.content.Context
import com.richard.pixlog.data.local.datastore.LoginPreferences
import com.richard.pixlog.data.local.datastore.dataStore
import com.richard.pixlog.data.remote.retrofit.ApiConfig
import com.richard.pixlog.data.remote.retrofit.ApiService
import com.richard.pixlog.data.repository.PixlogRepository
import com.richard.pixlog.utils.AppExecutors

object Injection {
    fun provideRepository (context: Context): PixlogRepository {
        val dataStoreLogin = LoginPreferences.getInstance(context.dataStore)
        val appExecutors = AppExecutors()
        val apiService : ApiService = ApiConfig.getApiServuce(dataStoreLogin)
        return PixlogRepository.getInstance(apiService, appExecutors, dataStoreLogin)
    }
}