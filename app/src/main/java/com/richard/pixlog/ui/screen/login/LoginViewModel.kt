package com.richard.pixlog.ui.screen.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.richard.pixlog.data.local.datastore.LoginPreferences
import com.richard.pixlog.data.remote.response.LoginResponse
import com.richard.pixlog.data.remote.response.RegisterResponse
import com.richard.pixlog.data.repository.PixlogRepository
import com.richard.pixlog.data.repository.Result
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LoginViewModel(
    private val repository: PixlogRepository,
    private val loginPreferences: LoginPreferences,
) : ViewModel() {
    
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    private val _token = MutableLiveData<String>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult
    val token: LiveData<String> = _token

    fun getToken() {
        viewModelScope.launch {
            try {
                loginPreferences.getToken().collect { token ->
                    _token.value = token
                    Log.d("LoginViewModel", "Token retrieved: $token")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error getting token", e)
                _token.value = ""
            }
        }
    }

    fun postLogin(username: String, email: String) {
        viewModelScope.launch {
            _loginResult.value = Result.Loading
            try {
                val response = repository.postLogin(username, email)
                
                // Save token if login successful
                if (!response.error) {
                    loginPreferences.saveDataLogin(response)
                    Log.d("LoginViewModel", "Token saved: ${response.loginResult.token}")
                }
                
                _loginResult.postValue(Result.Success(response))
                
                // Refresh token after successful login
                getToken()
                
            } catch (e: Exception) {
                val errorMessage = when (e) {
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
                _loginResult.postValue(Result.Error(errorMessage))
            }
        }
    }

}