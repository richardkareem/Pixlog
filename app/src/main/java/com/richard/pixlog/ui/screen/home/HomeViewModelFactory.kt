package com.richard.pixlog.ui.screen.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.richard.pixlog.data.di.Injection
import com.richard.pixlog.data.repository.PixlogRepository

class HomeViewModelFactory (
    private val repository:PixlogRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("class model unknown")
    }

    companion object{
        @Volatile
        private var instance : HomeViewModelFactory ? = null

        fun getInstance(context: Context) : HomeViewModelFactory =
            instance ?: synchronized(this){
                instance ?: HomeViewModelFactory(Injection .provideRepository(context))
            }.also { instance = it }

    }
}