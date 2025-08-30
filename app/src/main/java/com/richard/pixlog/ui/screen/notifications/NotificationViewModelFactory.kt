package com.richard.pixlog.ui.screen.notifications

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.richard.pixlog.data.local.datastore.LoginPreferences
import com.richard.pixlog.data.local.datastore.dataStore

class NotificationViewModelFactory(
    private val loginPreferences: LoginPreferences,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
            return NotificationsViewModel(loginPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    
    companion object {
        @Volatile
        private var instance: NotificationViewModelFactory? = null

        fun getInstance(context: Context): NotificationViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: NotificationViewModelFactory(
                    LoginPreferences.getInstance(context.dataStore)
                ).also { instance = it }
            }
    }
}