package com.ztute.memereview.network

import com.ztute.memereview.database.DatabaseMeme
import com.ztute.memereview.database.MemeDao
import com.ztute.memereview.domain.repository.MemeRepository
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