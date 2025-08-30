package com.richard.pixlog.ui.screen.notifications

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.richard.pixlog.data.local.datastore.LoginPreferences
import kotlinx.coroutines.launch

class NotificationsViewModel(private val loginPreferences: LoginPreferences) : ViewModel() {
    private  val _name = MutableLiveData<String>()
    private val _isCanNavigate = MutableLiveData<Boolean>(false)
    val isCanNavigate : LiveData<Boolean> = _isCanNavigate
    val name = _name
    fun logout(){
        viewModelScope.launch {
            loginPreferences.clearToken()
            loginPreferences.getToken().collect { token ->
                if (token.isEmpty()) {
                _isCanNavigate.value = true
                }
            }
        }
    }
    fun getName(){
        viewModelScope.launch {
            loginPreferences.getName().collect { prefName ->
                Log.d("NotificationsViewModel", "Name from preferences: $prefName")
                _name.value = prefName
            }
        }
    }
    init {
        getName()
    }
}