package com.ztute.memereview.repository

import com.ztute.memereview.database.DatabaseMeme
import com.ztute.memereview.database.MemeDao
import com.ztute.memereview.network.*
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.spy
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class DefaultMemeRepositoryTest : TestCase() {

    lateinit var defaultMemeRepository: DefaultMemeRepository

    @Mock
    lateinit var memeDao: MemeDao

    @Mock
    lateinit var memeReviewApiService: MemeReviewApiService

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    public override fun setUp() {
        super.setUp()
        Dispatchers.setMain(mainThreadSurrogate)
        defaultMemeRepository =
            spy(DefaultMemeRepository(memeDao, memeReviewApiService))
    }

    @After
    public override fun tearDown() {
        super.tearDown()
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun refreshMemes() {
        kotlinx.coroutines.test.runTest {
            val networkMeme = MemeDto(2, 200, "id", "abc", "url", 200)
            val databaseMeme = DatabaseMeme("id", 2, 200, "abc", "url", 200)
            val memeNetworkResponse = MemeNetworkResponse(
                Data(
                    listOf(
                        networkMeme
                    )
                ), true
            )
            val response = Response.success(memeNetworkResponse)
            doReturn(response).whenever(memeReviewApiService).getMemes()

            defaultMemeRepository.refreshMemes()

            verify(memeReviewApiService).getMemes()
            verifyNoMoreInteractions()
        }
    }

    private fun verifyNoMoreInteractions() {
        verifyNoInteractions(
            memeDao,
            memeReviewApiService,
        )
    }
}