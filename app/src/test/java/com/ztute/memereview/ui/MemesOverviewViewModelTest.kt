package com.ztute.memereview.ui

import android.app.Application
import android.net.ConnectivityManager
import com.ztute.memereview.TestDispatchers
import com.ztute.memereview.database.DatabaseMeme
import com.ztute.memereview.domain.model.Meme
import com.ztute.memereview.domain.repository.MemeRepository
import com.ztute.memereview.domain.usecase.GetMemesFromCacheUseCase
import com.ztute.memereview.domain.usecase.GetMemesFromNetworkUseCase
import com.ztute.memereview.network.ResultWrapper
import com.ztute.memereview.ui.meme_overview.MemesOverviewViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import okhttp3.Headers
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MemesOverviewViewModelTest {

    @Mock
    lateinit var application: Application

    @Mock
    lateinit var connectivityManager: ConnectivityManager

    @Mock
    lateinit var memeRepository: MemeRepository

    @Mock
    lateinit var getMemesFromCacheUseCase: GetMemesFromCacheUseCase

    @Mock
    lateinit var getMemesFromNetworkUseCase: GetMemesFromNetworkUseCase

    lateinit var viewModel: MemesOverviewViewModel

    val testDispatchers = TestDispatchers()

    @Before
    fun setUp() {
        viewModel = spy(
            MemesOverviewViewModel(
                application,
                memeRepository,
                getMemesFromCacheUseCase,
                getMemesFromNetworkUseCase,
                testDispatchers
            )
        )
    }

    @Test
    fun `load memes from cache returns successful result`() {
        doReturn(
            flowOf(
                ResultWrapper.Loading,
                ResultWrapper.DatabaseSuccess(listOf<DatabaseMeme>())
            )
        ).whenever(getMemesFromCacheUseCase).invoke()

        viewModel.loadMemesFromCache()

        verify(getMemesFromCacheUseCase, times(2)).invoke()
        assertTrue(viewModel.memes.value.isEmpty())
    }

    @Test
    fun `load memes from cache causes error`() {
        doReturn(
            flowOf(
                ResultWrapper.Loading,
                ResultWrapper.DatabaseError
            )
        ).whenever(getMemesFromCacheUseCase).invoke()

        viewModel.loadMemesFromCache()

        verify(getMemesFromCacheUseCase, times(2)).invoke()
    }

    @Test
    fun `fetch memes from network returns successful result`() = runBlocking {
        doReturn(
            flowOf(
                ResultWrapper.Loading,
                ResultWrapper.NetworkSuccess(
                    listOf<Meme>(),
                    Headers.headersOf(),
                    201
                )
            )
        ).whenever(getMemesFromNetworkUseCase).invoke()

        viewModel.fetchAndCacheMemes()

        verify(getMemesFromCacheUseCase).invoke()
        assertTrue(viewModel.memes.value.isEmpty())
    }

    @Test
    fun `fetch memes from network fails due to network error`() {
        doReturn(
            flowOf(
                ResultWrapper.Loading,
                ResultWrapper.NetworkError
            )
        ).whenever(getMemesFromNetworkUseCase).invoke()

        viewModel.fetchAndCacheMemes()

        verify(getMemesFromCacheUseCase).invoke()
    }

    @Test
    fun `fetch memes from network fails due to server error`() {
        doReturn(
            flowOf(
                ResultWrapper.Loading,
                ResultWrapper.GenericError(500, "Server Error")
            )
        ).whenever(getMemesFromNetworkUseCase).invoke()

        viewModel.fetchAndCacheMemes()

        verify(getMemesFromCacheUseCase).invoke()
    }

    @Test
    fun `fetch and cache latest memes when connected back to internet`() =
        runBlocking {
            viewModel.internetStatusChanged(true)
            testDispatchers.testDispatcher.scheduler.advanceTimeBy(
                600
            )
        }
}