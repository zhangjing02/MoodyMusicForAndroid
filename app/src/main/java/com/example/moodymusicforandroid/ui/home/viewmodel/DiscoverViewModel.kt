package com.example.moodymusicforandroid.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moodymusicforandroid.base.BaseViewModel
import com.example.moodymusicforandroid.data.api.MoodyApiProvider
import com.example.moodymusicforandroid.data.model.Artist
import kotlinx.coroutines.launch

class DiscoverViewModel : BaseViewModel() {

    private val _artists = MutableLiveData<List<Artist>>()
    val artists: LiveData<List<Artist>> = _artists

    fun fetchArtists() {
        viewModelScope.launch {
            try {
                val response = MoodyApiProvider.apiService.getArtists()
                if (response.code == 200) {
                    _artists.value = response.data?.artists ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
