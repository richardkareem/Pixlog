package com.richard.pixlog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.richard.pixlog.data.local.datastore.LoginPreferences
import com.richard.pixlog.ui.screen.login.LoginViewModel
import kotlinx.coroutines.launch

class MainViewModel(
    private val loginPreferences: LoginPreferences
): ViewModel() {

    fun checkToken() : LiveData<Boolean> {
        return  loginPreferences.checkToken().asLiveData()
    }
}