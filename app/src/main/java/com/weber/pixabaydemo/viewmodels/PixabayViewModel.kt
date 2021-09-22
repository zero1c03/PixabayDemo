package com.weber.pixabaydemo.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.weber.pixabaydemo.data.Hits
import com.weber.pixabaydemo.data.PixbayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PixabayViewModel @Inject constructor(private val repository: PixbayRepository) : ViewModel() {

    private var remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    var queryString: MutableLiveData<String> = MutableLiveData()
    var layoutType: MutableLiveData<Int> = MutableLiveData(LayoutType.GRID.ordinal)
    var autoCompleteList: MutableList<String> = mutableListOf()

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 5
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    fun getPhotos(): Flow<PagingData<Hits>> {
        return repository.getPhoto(queryString.value).cachedIn(viewModelScope)
    }

    fun getFireBaseLayoutType(): Int {
        val type = remoteConfig.getLong(PIXABAY_LAYOUTMANAGER_KEY)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val updated = it.result
                    Log.d("PixabayViewModel ---", "Config params updated: $updated")
                } else {
                    Log.d("PixabayViewModel ---", "Config params updated fail")
                }
            }.addOnFailureListener {
                it.printStackTrace()
            }
        return type.toInt()
    }


    companion object {
        enum class LayoutType {
            LIST,
            GRID
        }

        private const val PIXABAY_LAYOUTMANAGER_KEY = "pixbay_layoutmanager"
    }

}