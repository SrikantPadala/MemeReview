package com.ztute.memereview.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ztute.memereview.DispatcherProvider
import com.ztute.memereview.common.InternetStatus
import com.ztute.memereview.common.asDatabaseModel
import com.ztute.memereview.common.getNetworkCallback
import com.ztute.memereview.common.networkRequest
import com.ztute.memereview.database.asDomainModel
import com.ztute.memereview.domain.model.Meme
import com.ztute.memereview.domain.repository.MemeRepository
import com.ztute.memereview.domain.usecase.GetMemesFromCacheUseCase
import com.ztute.memereview.domain.usecase.GetMemesFromNetworkUseCase
import com.ztute.memereview.network.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MemesOverviewViewModel @Inject constructor(
    application: Application,
    private val memeRepository: MemeRepository,
    private val getMemesFromCacheUseCase: GetMemesFromCacheUseCase,
    private val getMemesFromNetworkUseCase: GetMemesFromNetworkUseCase,
    private val dispatchers: DispatcherProvider
) : AndroidViewModel(application), InternetStatus {

    private val _isLoading = MutableSharedFlow<Boolean>()
    val isLoading = _isLoading.asSharedFlow()

    private val _hasInternet = MutableSharedFlow<Boolean>()
    val hasInternet = _hasInternet.asSharedFlow()

    private val _navigateToSelectedMeme = MutableSharedFlow<Meme>()
    val navigateToSelectedMeme = _navigateToSelectedMeme.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _memes: MutableStateFlow<List<Meme>> =
        MutableStateFlow(listOf())
    val memes: StateFlow<List<Meme>> = _memes.asStateFlow()

    private val connectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    init {
        connectivityManager?.requestNetwork(
            networkRequest,
            getNetworkCallback(::internetStatusChanged)
        )
        loadMemesFromCache()
    }

    fun fetchAndCacheMemes() {
        Timber.d("fetchAndCacheMemes invoked")
        getMemesFromNetworkUseCase().onEach { result ->
            when (result) {
                is ResultWrapper.Loading -> {
                    _isLoading.emit(true)
                }
                is ResultWrapper.NetworkError -> _hasInternet.emit(false)
                is ResultWrapper.GenericError -> {
                    _isLoading.emit(false)
                    _errorMessage.emit("code: ${result.code}: ${result.error}")
                }
                is ResultWrapper.NetworkSuccess -> {
                    _isLoading.emit(false)
                    memeRepository.cacheData(result.value.asDatabaseModel())
                    _memes.emit(result.value.sortedBy { it.id })
                }
            }
        }
            .launchIn(viewModelScope + dispatchers.main)
    }

    fun loadMemesFromCache() {
        getMemesFromCacheUseCase().onEach { result ->
            when (result) {
                is ResultWrapper.Loading -> Unit
                is ResultWrapper.DatabaseError -> {
                    _errorMessage.emit("Database error")
                }
                is ResultWrapper.DatabaseSuccess -> {
                    _memes.emit(result.value.asDomainModel().sortedBy { it.id })
                }
            }
        }.launchIn(viewModelScope + dispatchers.main)
    }

    fun displayMemeDetail(meme: Meme) {
        viewModelScope.launch(dispatchers.main) {
            _navigateToSelectedMeme.emit(meme)
        }
    }

    override fun internetStatusChanged(hasInternet: Boolean) {
        Timber.d("internetStatusChanged invoked")
        viewModelScope.launch(dispatchers.main) {
            _hasInternet.emit(hasInternet)
            delay(500)
            if (hasInternet) {
                fetchAndCacheMemes()
            }
        }
    }
}