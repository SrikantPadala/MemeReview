package com.ztute.memereview.domain.repository

import com.ztute.memereview.database.DatabaseMeme
import com.ztute.memereview.network.MemeNetworkResponse
import com.ztute.memereview.network.ResultWrapper

interface MemeRepository {
    suspend fun refreshMemes(): ResultWrapper<MemeNetworkResponse>

    suspend fun cacheData(memeList: List<DatabaseMeme>)

    suspend fun getMemes(): List<DatabaseMeme>
}