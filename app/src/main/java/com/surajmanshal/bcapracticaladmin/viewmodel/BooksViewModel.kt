package com.surajmanshal.bcapracticaladmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BooksViewModel : ViewModel() {

    private val _demoImages = MutableLiveData<MutableList<String>>(mutableListOf(""))
    val demoImages : LiveData<MutableList<String>> get() = _demoImages

    private val _demoImagesUrl = MutableLiveData<MutableList<String>>()
    val demoImagesUrl : LiveData<MutableList<String>> get() = _demoImagesUrl


    fun addImage(url : String){
        _demoImages.value?.apply {
            add(last())
            set(lastIndexOf(last())-1,url)
        }
        refreshDemoImages()
    }

    fun removeImage(url : String){
        _demoImages.value?.remove(url)
        refreshDemoImages()
    }

    private fun refreshDemoImages() {
        _demoImages.postValue(_demoImages.value)
    }

    fun addDemoImageUrl(url : String){
        _demoImagesUrl.value?.apply {
            add(url)
        }
        refreshDemoImageUrls()
    }

    private fun refreshDemoImageUrls() {
        _demoImagesUrl.postValue(_demoImagesUrl.value)
    }

}