package com.iksica.myapplication.feature.zgMeni

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iksica.myapplication.feature.zgMeni.dataModels.MenuResponse
import com.iksica.myapplication.feature.zgMeni.dataModels.Post
import com.iksica.myapplication.feature.zgMeni.repository.ZgMeniRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ZgMeniViewModel(val zgMeniRepository: ZgMeniRepository) : ViewModel() {

    val locationData: MutableLiveData<List<Post>?> = MutableLiveData<List<Post>?>(null)
    val menies: MutableLiveData<List<MenuResponse>?> = MutableLiveData<List<MenuResponse>?>(null)

    val menzaOpened: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)

    init {
        getZgMenies()
    }

    fun getZgMenies() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            Log.e("ZgMeniError", "Crash: ${e.message}", e)
        }) {
            locationData.postValue(zgMeniRepository.fetchLocationData())
            menies.postValue(zgMeniRepository.fetchMenies())
        }
    }

    fun openMenza() {
        menzaOpened.postValue(true)
    }

    fun closeMenza() {
        menzaOpened.postValue(false)
    }

}