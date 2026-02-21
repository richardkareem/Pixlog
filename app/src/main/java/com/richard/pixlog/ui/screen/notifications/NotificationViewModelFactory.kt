package com.richard.pixlog.ui.screen.notifications

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.richard.pixlog.data.di.Injection
import com.richard.pixlog.data.local.datastore.LoginPreferences
import com.richard.pixlog.data.local.datastore.dataStore
import com.richard.pixlog.data.repository.PixlogRepository

class NotificationViewModelFactory(
    private val repository: PixlogRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
            return NotificationsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    
    companion object {
        @Volatile
        private var instance: NotificationViewModelFactory? = null

        fun getInstance(context: Context): NotificationViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: NotificationViewModelFactory(
                    Injection.provideRepository(context)
                ).also { instance = it }
            }
    }
}