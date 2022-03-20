package com.ztute.memereview.domain.usecase

import app.cash.turbine.test
import com.ztute.memereview.TestDispatchers
import com.ztute.memereview.domain.repository.MemeRepository
import com.ztute.memereview.network.Data
import com.ztute.memereview.network.MemeDto
import com.ztute.memereview.network.MemeNetworkResponse
import com.ztute.memereview.network.ResultWrapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import okhttp3.Headers
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class GetMemesFromNetworkUseCaseTest {

    @Mock
    lateinit var memeRepository: MemeRepository

    @Mock
    lateinit var timeoutCancellationException: TimeoutCancellationException

    lateinit var getMemesFromNetworkUseCase: GetMemesFromNetworkUseCase

    val testDispatchers = TestDispatchers()

    @Before
    fun setUp() {
        getMemesFromNetworkUseCase =
            spy(GetMemesFromNetworkUseCase(memeRepository, testDispatchers))
    }

    @Test
    fun `get memes from server is successful`() = runBlocking {
        val networkMeme = MemeDto(2, 200, "id", "abc", "url", 200)
        val memeNetworkResponse = MemeNetworkResponse(
            Data(
                listOf(
                    networkMeme
                )
            ), true
        )
        doReturn(
            ResultWrapper.NetworkSuccess(
                memeNetworkResponse,
                Headers.headersOf(),
                200
            )
        ).whenever(memeRepository).refreshMemes()
        getMemesFromNetworkUseCase().test {
            val loading = awaitItem()
            assertTrue(loading is ResultWrapper.Loading)
            val result = awaitItem()
            assertTrue(result is ResultWrapper.NetworkSuccess)
            if (result is ResultWrapper.NetworkSuccess) {
                assertTrue(result.value.size == 1)
            }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `get memes from server fails due to network error`() = runBlocking {
        doReturn(ResultWrapper.NetworkError).whenever(memeRepository)
            .refreshMemes()
        getMemesFromNetworkUseCase().test {
            val loading = awaitItem()
            assertTrue(loading is ResultWrapper.Loading)

            val result = awaitItem()
            assertTrue(result is ResultWrapper.NetworkError)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `get memes from server fails due to server error`() = runBlocking {
        doReturn(ResultWrapper.GenericError(500, "Server error")).whenever(
            memeRepository
        ).refreshMemes()
        getMemesFromNetworkUseCase().test {
            val loading = awaitItem()
            assertTrue(loading is ResultWrapper.Loading)

            val result = awaitItem()
            assertTrue(result is ResultWrapper.GenericError)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `get memes from server fails due to timeout`() = runBlocking {
        doThrow(timeoutCancellationException).whenever(
            memeRepository
        ).refreshMemes()
        getMemesFromNetworkUseCase().test {
            val loading = awaitItem()
            assertTrue(loading is ResultWrapper.Loading)

            val result = awaitItem()
            assertTrue(result is ResultWrapper.GenericError)
            cancelAndConsumeRemainingEvents()
        }
    }
}