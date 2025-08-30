package com.richard.pixlog.ui.screen.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.richard.pixlog.data.di.Injection
import com.richard.pixlog.data.local.datastore.LoginPreferences
import com.richard.pixlog.data.local.datastore.dataStore
import com.richard.pixlog.data.repository.PixlogRepository

class LoginViewModelFactory (
    private val repository: PixlogRepository,
    private val loginPreferences: LoginPreferences
) : ViewModelProvider.NewInstanceFactory() {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository, loginPreferences) as T
        }
        throw IllegalArgumentException("class model unknown")
    }

    companion object {
        @Volatile
        private var instance: LoginViewModelFactory? = null

        fun getInstance(context: Context): LoginViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: LoginViewModelFactory(
                    Injection.provideRepository(context),
                    LoginPreferences.getInstance(context.dataStore)
                ).also { instance = it }
            }
    }
}