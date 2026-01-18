package com.securedoc.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.securedoc.app.drive.DriveImage
import com.securedoc.app.drive.DriveServiceHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StudentGalleryViewModel : ViewModel() {
    private val _images = MutableLiveData<List<DriveImage>>(emptyList())
    val images: LiveData<List<DriveImage>> = _images

    private var pollingJob: Job? = null

    fun startPolling(helper: DriveServiceHelper, folderId: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            var lastIds: Set<String> = emptySet()
            while (true) {
                val newImages = helper.listImages(folderId)
                val newIds = newImages.map { it.id }.toSet()
                if (newIds != lastIds) {
                    lastIds = newIds
                    _images.value = newImages
                }
                delay(60_000)
            }
        }
    }

    override fun onCleared() {
        pollingJob?.cancel()
        super.onCleared()
    }
}
