package com.richard.pixlog.ui.screen.map

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.richard.pixlog.data.local.entity.LocationStory
import com.richard.pixlog.data.remote.response.RegisterResponse
import com.richard.pixlog.data.repository.PixlogRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MapViewModel(private val repository: PixlogRepository) : ViewModel() {

    private var _storyWithLocation = MutableLiveData<List<LocationStory>>()
    var storyWithLocation = _storyWithLocation
     private fun getAllStoryWithLocation(){
         viewModelScope.launch {
             try {
                 val listStory = repository.getAllStoryWithLocation().listStory
                 val allLocation = listStory.map { story ->
                     LocationStory(
                         name = story.name,
                         lat = story.lat,
                         lon = story.lon,
                         img = story.photoUrl
                     )
                 }
                 Log.d("MapViewModel", "length data is ${allLocation.size}")
                 _storyWithLocation.postValue(allLocation)
             }catch(e: Exception){
                 val errorMessage = when(e){
                     is HttpException -> {
                         try {
                             val errorBody = e.response()?.errorBody()?.string()
                             val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                             errorResponse.message
                         } catch (e: Exception) {
                             "HTTP Error: ${e.message}"
                         }
                     }
                     is SocketTimeoutException -> "Timeout, coba lagi"
                     is UnknownHostException -> "Tidak ada internet"
                     is ConnectException -> "Gagal terhubung ke server"
                     is IOException -> "Error koneksi jaringan"
                     else -> "Error tidak diketahui: ${e.message}"
                 }
                 Log.e("LoginViewModel", "Login error: $errorMessage", e)
             }

         }
    }
    init {
        getAllStoryWithLocation()
    }
}