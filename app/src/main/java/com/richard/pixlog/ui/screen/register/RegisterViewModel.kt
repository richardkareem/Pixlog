package com.richard.pixlog.ui.screen.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.richard.pixlog.data.remote.response.LoginResponse
import com.richard.pixlog.data.remote.response.RegisterResponse
import com.richard.pixlog.data.repository.PixlogRepository
import com.richard.pixlog.data.repository.Result
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RegisterViewModel(
    private val repository: PixlogRepository,
) : ViewModel() {
    private val _resultRegister = MutableLiveData<Result<RegisterResponse>>()
    val resultRegister = _resultRegister

    fun postRegister(username: String, email: String, password: String) {
        _resultRegister.value = Result.Loading
        viewModelScope.launch {
            try {
                val response = repository.postRegister(username, email, password)
                _resultRegister.postValue(Result.Success(response))
            }catch (e: Exception) {
                val errorMessage =
                    when (e) {
                        is HttpException -> {
                            val errorBody = e.response()?.errorBody()?.string()
                            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                            errorResponse.message
                        }

                        is SocketTimeoutException -> {
                            "Timeout, coba lagi"
                        }

                        is UnknownHostException -> {
                            "Tidak ada internet"
                        }

                        else -> {
                            "Error Tidak Diketahui"
                        }
                    }
                _resultRegister.postValue(Result.Error(errorMessage))
            }


        }
    }
}