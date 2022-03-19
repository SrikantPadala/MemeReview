package com.ztute.memereview.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ztute.memereview.common.InternetStatus
import com.ztute.memereview.common.getNetworkCallback
import com.ztute.memereview.common.hasInternetConnection
import com.ztute.memereview.common.networkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(@ApplicationContext application: Application) :
    AndroidViewModel(application), InternetStatus {
    private val _hasInternet =
        MutableStateFlow<Boolean>(hasInternetConnection(application))
    val hasInternet = _hasInternet.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading = _isLoading.asStateFlow()

    val connectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    init {
        viewModelScope.launch {
            delay(3000)
            _isLoading.value = false
        }
        connectivityManager.requestNetwork(
            networkRequest,
            getNetworkCallback(::internetStatusChanged)
        )
    }

    override fun internetStatusChanged(hasInternet: Boolean) {
        viewModelScope.launch {
            _hasInternet.emit(hasInternet)
        }
    }
}