package com.ztute.memereview.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ztute.memereview.domain.model.Meme

class MemeDetailViewModel() : ViewModel() {
    private val _meme = MutableLiveData<Meme>()
    val meme: LiveData<Meme> = _meme

    fun setData(meme: Meme) {
        _meme.value = meme
    }
}