package com.ztute.memereview.repository

import com.ztute.memereview.database.DatabaseMeme
import com.ztute.memereview.database.MemeDao
import com.ztute.memereview.network.MemeNetworkResponse
import com.ztute.memereview.network.MemeReviewApiService
import com.ztute.memereview.network.ResultWrapper
import com.ztute.memereview.network.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class DefaultMemeRepository @Inject constructor(
    private val memeDao: MemeDao,
    private val memeReviewApiService: MemeReviewApiService
) : MemeRepository {

    override suspend fun refreshMemes(): ResultWrapper<MemeNetworkResponse> {
        return safeApiCall(Dispatchers.IO) { memeReviewApiService.getMemes() }
    }

    override suspend fun cacheData(memeList: List<DatabaseMeme>) {
        memeDao.insertAll(*memeList.toTypedArray())
    }

    override suspend fun getMemes(): List<DatabaseMeme> = memeDao.getMemes()
}