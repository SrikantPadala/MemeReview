package com.ztute.memereview.network

import com.ztute.memereview.database.DatabaseMeme
import com.ztute.memereview.database.MemeDao
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DefaultMemeRepositoryTest {

    lateinit var defaultMemeRepository: DefaultMemeRepository

    @Mock
    lateinit var memeDao: MemeDao

    @Mock
    lateinit var memeReviewApiService: MemeReviewApiService

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        defaultMemeRepository =
            spy(DefaultMemeRepository(memeDao, memeReviewApiService))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun `refresh Memes from network returns successful result`() = runTest {
        val networkMeme = MemeDto(2, 200, "id", "abc", "url", 200)
        val memeNetworkResponse = MemeNetworkResponse(
            Data(
                listOf(
                    networkMeme
                )
            ), true
        )
        val response = Response.success(memeNetworkResponse)
        doReturn(response).whenever(memeReviewApiService).getMemes()

        val resultWrapper = defaultMemeRepository.refreshMemes()
        assertTrue(resultWrapper is ResultWrapper.NetworkSuccess)

        verify(memeReviewApiService).getMemes()
    }

    @Test
    fun `cache data`() = runTest {
        defaultMemeRepository.cacheData(listOf())
        verify(memeDao).insertAll(anyVararg())
    }

    @Test
    fun `get memes from cache`() = runTest {
        doReturn(listOf<DatabaseMeme>()).whenever(memeDao).getMemes()

        defaultMemeRepository.getMemes()
        verify(memeDao).getMemes()
    }
}