package com.richard.pixlog.ui.screen.notifications

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.richard.pixlog.data.local.datastore.LoginPreferences
import com.richard.pixlog.data.repository.PixlogRepository
import kotlinx.coroutines.launch

class NotificationsViewModel(private val repository: PixlogRepository) : ViewModel() {
    private  val _name = MutableLiveData<String>()
    private val _isCanNavigate = MutableLiveData<Boolean>(false)
    val isCanNavigate : LiveData<Boolean> = _isCanNavigate
    val name = _name
    fun logout(){
        viewModelScope.launch {
            repository.logout()
            _isCanNavigate.postValue(true)
        }
    }
    fun getName(){
        viewModelScope.launch {
            repository.getNamePreferences().collect {
                prefName ->
                Log.d("NotificationsViewModel", "Name from preferences: $prefName")
                _name.value = prefName
            }
        }
    }
    init {
        getName()
    }
}