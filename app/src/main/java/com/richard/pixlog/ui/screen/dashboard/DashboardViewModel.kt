package com.richard.pixlog.ui.screen.dashboard

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.richard.pixlog.data.remote.response.UploadResponse
import com.richard.pixlog.data.repository.PixlogRepository
import com.richard.pixlog.data.repository.Result
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class DashboardViewModel(
    private val repository: PixlogRepository
) : ViewModel() {

    private val _result = MutableLiveData<Result<UploadResponse>>()
    val result = _result

    fun uploadStory(file: MultipartBody.Part, requestBody: RequestBody){
        _result.value = Result.Loading
        viewModelScope.launch {
            try {
                val response = repository.uploadStory(file, requestBody)
                if(response.error){
                    _result.value = Result.Error(response.message)
                }else{
                    _result.value = Result.Success(response)
                }
            }catch (e: HttpException){
                if(e.response() != null){
                    Log.e(DashboardViewModel::class.java.simpleName, "${e.response()?.errorBody()?.string()}")
                }
                _result.value = Result.Error("Upload failed")
            }catch (e: UnknownHostException) {
                Log.e("DashboardViewModel", "Network error: UnknownHostException", e)
                _result.value = Result.Error("Tidak dapat terhubung ke server. Periksa koneksi internet Anda.")
            }catch (e: SocketTimeoutException) {
                Log.e("DashboardViewModel", "Network error: SocketTimeoutException", e)
                _result.value = Result.Error("Koneksi timeout. Silakan coba lagi.")
            }catch (e: ConnectException) {
                Log.e("DashboardViewModel", "Network error: ConnectException", e)
                _result.value = Result.Error("Gagal terhubung ke server. Periksa koneksi internet Anda.")
            }catch (e: IOException) {
                Log.e("DashboardViewModel", "Network error: IOException", e)
                _result.value = Result.Error("Error koneksi jaringan. Silakan coba lagi.")
            }catch (e: Exception) {
                Log.e("DashboardViewModel", "Unexpected error", e)
                _result.value = Result.Error("Terjadi kesalahan yang tidak terduga. Silakan coba lagi.")
            }
        }
    }
}