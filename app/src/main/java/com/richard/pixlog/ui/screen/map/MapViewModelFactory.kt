package com.richard.pixlog.ui.screen.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.richard.pixlog.data.di.Injection
import com.richard.pixlog.data.repository.PixlogRepository

class MapViewModelFactory(
    private val repository: PixlogRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(repository) as T
        }
        throw IllegalArgumentException("class model unknown")
    }

    companion object {
        @Volatile
        private var instance: MapViewModelFactory? = null

        fun getInstance(context: Context): MapViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: MapViewModelFactory(
                    Injection.provideRepository(context),
                ).also { instance = it }
            }
    }
}