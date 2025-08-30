package com.richard.pixlog.ui.screen.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.richard.pixlog.data.di.Injection
import com.richard.pixlog.data.repository.PixlogRepository


class RegisterViewModelFactory (
    private val repository: PixlogRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("class model unknown")
    }

    companion object{
        @Volatile
        private var instance : RegisterViewModelFactory ? = null

        fun getInstance(context: Context) : RegisterViewModelFactory =
            instance ?: synchronized(this){
                instance ?: RegisterViewModelFactory(Injection .provideRepository(context))
            }.also { instance = it }

    }
}