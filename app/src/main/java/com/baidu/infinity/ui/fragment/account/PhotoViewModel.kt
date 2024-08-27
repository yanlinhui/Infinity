package com.baidu.infinity.ui.fragment.account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baidu.infinity.ui.fragment.account.album.PhotoModel
import com.baidu.infinity.ui.fragment.account.album.loadPhotoModels

class PhotoViewModel: ViewModel() {
    val photoModels: MutableLiveData<ArrayList<PhotoModel>> = MutableLiveData(loadPhotoModels())
    var selectedIndex = 0

    fun reloadData(){
        photoModels.value = loadPhotoModels()
    }

    fun removeAll(models: List<PhotoModel>){
        photoModels.value?.removeAll(models)
    }
    fun remove(model: PhotoModel){
        photoModels.value?.remove(model)
    }
}