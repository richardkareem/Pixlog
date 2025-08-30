package com.richard.pixlog.data.repository

sealed class Result<out T> private constructor(){
    data class Success<out T>(val data: T): Result<T>()
    data class Error(val error: String): Result<Nothing>()
    object Loading : Result<Nothing>()
}