package com.ztute.memereview.domain.usecase

import app.cash.turbine.test
import com.ztute.memereview.TestDispatchers
import com.ztute.memereview.database.DatabaseMeme
import com.ztute.memereview.domain.repository.MemeRepository
import com.ztute.memereview.network.ResultWrapper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.spy
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GetMemesFromCacheUseCaseTest {

    @Mock
    lateinit var memeRepository: MemeRepository

    @Mock
    lateinit var databaseMeme: DatabaseMeme

    lateinit var getMemesFromCacheUseCase: GetMemesFromCacheUseCase

    @Before
    fun setUp() {
        getMemesFromCacheUseCase =
            spy(GetMemesFromCacheUseCase(memeRepository, TestDispatchers()))
    }

    @Test
    fun `get memes from cache is successful`() = runBlocking {
        doReturn(listOf(databaseMeme)).whenever(memeRepository).getMemes()
        getMemesFromCacheUseCase().test {
            val loading = awaitItem()
            assertTrue(loading is ResultWrapper.Loading)

            val result = awaitItem()
            assertTrue(result is ResultWrapper.DatabaseSuccess)
            if (result is ResultWrapper.DatabaseSuccess) {
                assertTrue(result.value.size == 1)
            }
            cancelAndConsumeRemainingEvents()
        }
        verify(memeRepository).getMemes()
        Unit
    }
}