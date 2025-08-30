package com.richard.pixlog.ui.screen.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.richard.pixlog.data.di.Injection
import com.richard.pixlog.data.repository.PixlogRepository

class DashboardViewModelFactory (
    private val repository:PixlogRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DashboardViewModel::class.java)){
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("class model unknown")
    }

    companion object{
        @Volatile
        private var instance : DashboardViewModelFactory ? = null

        fun getInstance(context: Context) : DashboardViewModelFactory =
            instance ?: synchronized(this){
                instance ?: DashboardViewModelFactory(Injection .provideRepository(context))
            }.also { instance = it }

    }
}