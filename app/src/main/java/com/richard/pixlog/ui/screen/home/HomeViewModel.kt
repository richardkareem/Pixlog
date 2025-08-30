package com.richard.pixlog.ui.screen.home
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.richard.pixlog.data.remote.response.ListStory
import com.richard.pixlog.data.repository.PixlogRepository

class HomeViewModel(
    private val repository: PixlogRepository,
) : ViewModel() {

    val getStory: LiveData<PagingData<ListStory>> =
        repository.getAllStory().cachedIn(viewModelScope)
}